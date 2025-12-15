package com.example.cacex.service.apply.itemapply;

import com.example.cacex.exception.InvalidPlanItemException;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.model.Action;
import com.example.cacex.model.PlanItem;
import com.example.cacex.service.apply.apiservice.PlanItemActionService;

public abstract class AbstractPlanItemApplier<T> implements PlanItemApplier {



    private final Class<T> payloadType;
    private final PlanItemActionService<T> apiService;

    /**
     * Builds an applier that delegates CRUD actions to the provided API service.
     *
     * @param payloadType expected payload type
     * @param apiService  service performing the underlying API calls
     */
    protected AbstractPlanItemApplier(Class<T> payloadType, PlanItemActionService<T> apiService) {
        this.payloadType = payloadType;
        this.apiService = apiService;
    }

    /**
     * Applies the plan item, delegating to create/update/delete and enforcing payload type safety.
     *
     * @param item plan item to apply
     */
    @Override
    public void apply(PlanItem item) {
        try {
            T payload = extractPayload(item);
            Action action = item.getAction();
            if (action == null) {
                throw new InvalidPlanItemException("Plan item action cannot be null");
            }
            switch (action) {
                case NEW -> apiService.create(item.getScope(), item.getKey(), payload);
                case UPDATE -> apiService.update(item.getScope(), item.getKey(), payload);
                case DELETE -> apiService.delete(item.getScope(), item.getKey(), payload);
                default -> throw new InvalidPlanItemException("Unsupported action " + action);
            }
        } catch (InvalidPlanItemException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new PlanApplyException("Failed to apply plan item " + item.getKey(), ex);
        }
    }

    private T extractPayload(PlanItem item) {
        Object payload = item.getPayload();
        if (payload == null) {
            return null;
        }
        if (!payloadType.isInstance(payload)) {
            throw new InvalidPlanItemException("Expected payload of type " + payloadType.getSimpleName()
                    + " but found " + payload.getClass().getSimpleName());
        }
        return payloadType.cast(payload);
    }

}
