package com.example.cacex.service.plan.rules;

import com.example.cacex.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanOrderingRuleEngineTest {

    @Test
    void appliesConfiguredOrderingAndTransactionSequence() {
        PlanOrderingProperties properties = new PlanOrderingProperties();
        PlanOrderingProperties.Rule rule = new PlanOrderingProperties.Rule();
        rule.setCategory(FileCategory.TRANSACTION);
        rule.setActions(List.of(Action.UPDATE, Action.NEW, Action.DELETE));
        rule.setSortTransactionSequence(true);
        properties.setRules(List.of(rule));

        PlanOrderingRuleEngine engine = new PlanOrderingRuleEngine(properties);

        TransactionFile txn1 = new TransactionFile();
        txn1.setTransactionSequence(5);
        TransactionFile txn2 = new TransactionFile();
        txn2.setTransactionSequence(1);

        PlanItem late = new PlanItem(Action.UPDATE, FileCategory.TRANSACTION, "S", "L", "p", txn1);
        PlanItem early = new PlanItem(Action.UPDATE, FileCategory.TRANSACTION, "S", "E", "p", txn2);
        PlanItem create = new PlanItem(Action.NEW, FileCategory.TRANSACTION, "S", "N", "p", txn2);

        MasterPlan plan = new MasterPlan();
        plan.addItem(late);
        plan.addItem(create);
        plan.addItem(early);

        MasterPlan sorted = engine.applyOrdering(plan);
        assertEquals(List.of(early, late, create), sorted.getItems());
    }

    @Test
    void fallsBackToDefaultOrdering() {
        PlanOrderingRuleEngine engine = new PlanOrderingRuleEngine(new PlanOrderingProperties());
        PlanItem sideDelete = new PlanItem(Action.DELETE, FileCategory.SIDE, "S", "D", "p", null);
        PlanItem sideCreate = new PlanItem(Action.NEW, FileCategory.SIDE, "S", "C", "p", null);
        MasterPlan plan = new MasterPlan();
        plan.addItem(sideDelete);
        plan.addItem(sideCreate);

        MasterPlan sorted = engine.applyOrdering(plan);
        assertEquals(List.of(sideCreate, sideDelete), sorted.getItems());
    }

    @Test
    void handlesNullOrUnmatchedItemsWithFallbackOrdering() {
        PlanOrderingProperties.Rule nullRule = new PlanOrderingProperties.Rule();
        nullRule.setCategory(null);
        PlanOrderingProperties.Rule sideRule = new PlanOrderingProperties.Rule();
        sideRule.setCategory(FileCategory.SIDE);
        // leave actions null to exercise defaults
        PlanOrderingProperties properties = new PlanOrderingProperties();
        properties.setRules(List.of(nullRule, sideRule));

        PlanOrderingRuleEngine engine = new PlanOrderingRuleEngine(properties);
        PlanItem firstSide = new PlanItem(Action.UPDATE, FileCategory.SIDE, "S", "A", "p", null);
        PlanItem secondSide = new PlanItem(Action.UPDATE, FileCategory.SIDE, "S", "B", "p", null);
        PlanItem unmatched = new PlanItem(Action.NEW, FileCategory.ABOR, "S", "C", "p", null);
        PlanItem missingCategory = new PlanItem(Action.DELETE, null, "S", "D", "p", null);

        MasterPlan plan = new MasterPlan();
        plan.addItem(firstSide);
        plan.addItem(missingCategory);
        plan.addItem(unmatched);
        plan.addItem(secondSide);

        MasterPlan sorted = engine.applyOrdering(plan);
        assertEquals(List.of(firstSide, secondSide, unmatched, missingCategory), sorted.getItems());
    }

    @Test
    void returnsPlanWhenEmptyOrNull() {
        PlanOrderingRuleEngine engine = new PlanOrderingRuleEngine(new PlanOrderingProperties());

        assertEquals(null, engine.applyOrdering(null));
        MasterPlan empty = new MasterPlan();
        assertEquals(empty, engine.applyOrdering(empty));
    }
}
