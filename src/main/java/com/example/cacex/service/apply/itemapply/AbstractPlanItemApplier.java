package com.example.cacex.service.apply.itemapply;

import com.example.cacex.exception.InvalidPlanItemException;
import com.example.cacex.exception.PlanApplyException;
import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;
import com.example.cacex.service.apply.apiservice.PlanItemActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlanItemApplier<T> implements PlanItemApplier {

    private static final Logger log = LoggerFactory.getLogger(AbstractPlanItemApplier.class);

    private final Class<T> payloadType;
    private final PlanItemActionService<T> apiService;

    protected AbstractPlanItemApplier(Class<T> payloadType, PlanItemActionService<T> apiService) {
        this.payloadType = payloadType;
        this.apiService = apiService;
    }

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
            log.error("Invalid plan item {} for category {}: {}", item.getKey(), item.getFileCategory(),
                    ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to apply plan item {} for category {}", item.getKey(), item.getFileCategory(), ex);
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

    @Override
    public abstract FileCategory supportedCategory();
}
