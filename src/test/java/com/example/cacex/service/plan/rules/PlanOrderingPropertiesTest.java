package com.example.cacex.service.plan.rules;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanOrderingPropertiesTest {

    @Test
    void gettersAndSettersWork() {
        PlanOrderingProperties properties = new PlanOrderingProperties();
        PlanOrderingProperties.Rule rule = new PlanOrderingProperties.Rule();
        rule.setCategory(FileCategory.SIDE);
        rule.setActions(List.of(Action.NEW));
        rule.setSortTransactionSequence(true);

        properties.setRules(List.of(rule));

        assertEquals(1, properties.getRules().size());
        PlanOrderingProperties.Rule stored = properties.getRules().get(0);
        assertEquals(FileCategory.SIDE, stored.getCategory());
        assertEquals(List.of(Action.NEW), stored.getActions());
        assertEquals(true, stored.isSortTransactionSequence());
    }
}
