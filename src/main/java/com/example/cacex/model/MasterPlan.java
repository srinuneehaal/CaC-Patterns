package com.example.cacex.model;

import java.util.ArrayList;
import java.util.List;

public class MasterPlan {

    private List<PlanItem> items = new ArrayList<>();

    public List<PlanItem> getItems() {
        return items;
    }

    public void setItems(List<PlanItem> items) {
        this.items = items;
    }

    public void addItem(PlanItem item) {
        this.items.add(item);
    }
}
