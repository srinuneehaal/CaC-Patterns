package com.example.cacex.service.apply.apiservice;

public interface PlanItemActionService<T> {

    void create(String scope, String key, T payload);

    void update(String scope, String key, T payload);

    void delete(String scope, String key, T payload);
}
