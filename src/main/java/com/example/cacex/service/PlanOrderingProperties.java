package com.example.cacex.service;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration holder for plan ordering rules. The list is read from
 * application.properties so the execution order can be tuned without code changes.
 */
@Component
@ConfigurationProperties(prefix = "plan.ordering")
public class PlanOrderingProperties {

    private List<Rule> rules = new ArrayList<>();

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static class Rule {
        private FileCategory category;
        private List<Action> actions = new ArrayList<>();
        private boolean sortTransactionSequence;

        public FileCategory getCategory() {
            return category;
        }

        public void setCategory(FileCategory category) {
            this.category = category;
        }

        public List<Action> getActions() {
            return actions;
        }

        public void setActions(List<Action> actions) {
            this.actions = actions;
        }

        public boolean isSortTransactionSequence() {
            return sortTransactionSequence;
        }

        public void setSortTransactionSequence(boolean sortTransactionSequence) {
            this.sortTransactionSequence = sortTransactionSequence;
        }
    }
}
