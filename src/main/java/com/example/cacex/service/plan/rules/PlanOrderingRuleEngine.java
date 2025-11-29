package com.example.cacex.service.plan.rules;

import com.example.cacex.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Rule engine that orders plan items using a configurable list of category/action rules.
 * Rules are read from {@link PlanOrderingProperties}, so the execution order can be tweaked
 * through application.properties without touching code. A stable tie-breaker preserves the
 * original discovery order when no explicit preference is provided.
 */
@Service
public class PlanOrderingRuleEngine {

    private static final List<Action> DEFAULT_ACTION_ORDER = List.of(Action.NEW, Action.UPDATE, Action.DELETE);
    private static final int NO_SEQUENCE_WEIGHT = Integer.MAX_VALUE;

    private final PlanOrderingProperties properties;

    public PlanOrderingRuleEngine(PlanOrderingProperties properties) {
        this.properties = properties;
    }

    public MasterPlan applyOrdering(MasterPlan plan) {
        if (plan == null || plan.getItems() == null || plan.getItems().isEmpty()) {
            return plan;
        }

        List<PlanOrderingRule> rules = resolveRules();
        List<PlanItem> sorted = new ArrayList<>(plan.getItems());
        Map<PlanItem, Integer> originalOrder = indexByIdentity(sorted);
        sorted.sort((left, right) -> buildOrderKey(left, rules, originalOrder)
                .compareTo(buildOrderKey(right, rules, originalOrder)));
        plan.setItems(sorted);
        return plan;
    }

    private List<PlanOrderingRule> resolveRules() {
        List<PlanOrderingRule> configured = properties.getRules().stream()
                .filter(rule -> rule.getCategory() != null)
                .map(this::toRule)
                .toList();
        if (!configured.isEmpty()) {
            return configured;
        }
        return defaultRules();
    }

    private PlanOrderingRule toRule(PlanOrderingProperties.Rule source) {
        List<Action> actions = source.getActions() == null || source.getActions().isEmpty()
                ? DEFAULT_ACTION_ORDER
                : List.copyOf(source.getActions());
        return new PlanOrderingRule(source.getCategory(), actions, source.isSortTransactionSequence());
    }

    private OrderKey buildOrderKey(PlanItem item,
                                   List<PlanOrderingRule> rules,
                                   Map<PlanItem, Integer> originalOrder) {
        for (int i = 0; i < rules.size(); i++) {
            PlanOrderingRule rule = rules.get(i);
            if (rule.matches(item)) {
                return new OrderKey(
                        i,
                        rule.actionIndex(item.getAction()),
                        rule.sequenceWeight(item),
                        originalOrder.getOrDefault(item, Integer.MAX_VALUE)
                );
            }
        }
        int fallbackActionIndex = DEFAULT_ACTION_ORDER.indexOf(item.getAction());
        if (fallbackActionIndex < 0) {
            fallbackActionIndex = DEFAULT_ACTION_ORDER.size();
        }
        return new OrderKey(
                rules.size(),
                fallbackActionIndex,
                NO_SEQUENCE_WEIGHT,
                originalOrder.getOrDefault(item, Integer.MAX_VALUE)
        );
    }

    private Map<PlanItem, Integer> indexByIdentity(List<PlanItem> items) {
        Map<PlanItem, Integer> order = new IdentityHashMap<>();
        for (int i = 0; i < items.size(); i++) {
            order.put(items.get(i), i);
        }
        return order;
    }

    private List<PlanOrderingRule> defaultRules() {
        List<PlanOrderingRule> defaults = new ArrayList<>();
        // 1. create/update side
        defaults.add(new PlanOrderingRule(FileCategory.SIDE, List.of(Action.NEW, Action.UPDATE), false));
        // 2. delete/update/create transactions (sorted by transactionSequence)
        defaults.add(new PlanOrderingRule(FileCategory.TRANSACTION, List.of(Action.DELETE, Action.UPDATE, Action.NEW), true));
        // 3. delete side
        defaults.add(new PlanOrderingRule(FileCategory.SIDE, List.of(Action.DELETE), false));
        // 4. create/update/delete chart of accounts
        defaults.add(new PlanOrderingRule(FileCategory.CHART_OF_ACCOUNTS, List.of(Action.NEW, Action.UPDATE, Action.DELETE), false));
        // 5. create/update/delete account
        defaults.add(new PlanOrderingRule(FileCategory.ACCOUNT, List.of(Action.NEW, Action.UPDATE, Action.DELETE), false));
        // 6. create/update/delete posting rules
        defaults.add(new PlanOrderingRule(FileCategory.POSTING_RULE, List.of(Action.NEW, Action.UPDATE, Action.DELETE), false));
        // 7. create/update/delete ABOR configuration
        defaults.add(new PlanOrderingRule(FileCategory.ABOR_CONFIGURATION, List.of(Action.NEW, Action.UPDATE, Action.DELETE), false));
        // 8. create/update portfolios
        defaults.add(new PlanOrderingRule(FileCategory.DERIVED_PORTFOLIO, List.of(Action.NEW, Action.UPDATE), false));
        // 9. create/update/delete portfolio groups
        defaults.add(new PlanOrderingRule(FileCategory.PORTFOLIO_GROUP, List.of(Action.NEW, Action.UPDATE, Action.DELETE), false));
        // 10. delete portfolios
        defaults.add(new PlanOrderingRule(FileCategory.DERIVED_PORTFOLIO, List.of(Action.DELETE), false));
        return defaults;
    }

    private record OrderKey(int groupOrder, int actionOrder, int transactionSequence, int originalOrder)
            implements Comparable<OrderKey> {

        @Override
        public int compareTo(OrderKey other) {
            int result = Integer.compare(groupOrder, other.groupOrder);
            if (result != 0) {
                return result;
            }
            result = Integer.compare(actionOrder, other.actionOrder);
            if (result != 0) {
                return result;
            }
            result = Integer.compare(transactionSequence, other.transactionSequence);
            if (result != 0) {
                return result;
            }
            return Integer.compare(originalOrder, other.originalOrder);
        }
    }

    private static final class PlanOrderingRule {
        private final FileCategory category;
        private final List<Action> actions;
        private final boolean sortTransactionSequence;

        PlanOrderingRule(FileCategory category, List<Action> actions, boolean sortTransactionSequence) {
            this.category = category;
            this.actions = actions == null || actions.isEmpty() ? DEFAULT_ACTION_ORDER : actions;
            this.sortTransactionSequence = sortTransactionSequence;
        }

        boolean matches(PlanItem item) {
            if (item == null || item.getFileCategory() == null || item.getAction() == null) {
                return false;
            }
            if (!Objects.equals(category, item.getFileCategory())) {
                return false;
            }
            return actions.isEmpty() || actions.contains(item.getAction());
        }

        int actionIndex(Action action) {
            int index = actions.indexOf(action);
            return index >= 0 ? index : actions.size();
        }

        int sequenceWeight(PlanItem item) {
            if (!sortTransactionSequence) {
                return NO_SEQUENCE_WEIGHT;
            }
            Object payload = item.getPayload();
            if (payload instanceof TransactionFile transaction && transaction.getTransactionSequence() != null) {
                return transaction.getTransactionSequence();
            }
            return NO_SEQUENCE_WEIGHT;
        }
    }
}
