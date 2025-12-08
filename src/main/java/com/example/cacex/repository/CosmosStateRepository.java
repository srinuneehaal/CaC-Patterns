package com.example.cacex.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.cacex.model.StateDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Cosmos DB implementation of {@link StateRepository}.
 */
@Service
public class CosmosStateRepository implements StateRepository {

    private final CosmosContainer container;

    public CosmosStateRepository(CosmosContainer container) {
        this.container = container;
    }

    @Override
    public Optional<StateDocument> find(String id, String typeOfItem) {
        try {
            return Optional.ofNullable(container.readItem(id, new PartitionKey(typeOfItem), StateDocument.class)
                    .getItem());
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw e;
        }
    }

    @Override
    public List<StateDocument> list(String typeOfItem, String scope) {
        StringBuilder query = new StringBuilder("SELECT * FROM c WHERE c.typeOfItem = @typeOfItem");
        List<SqlParameter> parameters = new ArrayList<>();
        parameters.add(new SqlParameter("@typeOfItem", typeOfItem));
        if (scope != null && !scope.isBlank()) {
            query.append(" AND c.scope = @scope");
            parameters.add(new SqlParameter("@scope", scope));
        }
        SqlQuerySpec spec = new SqlQuerySpec(query.toString(), parameters);
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions()
                .setPartitionKey(new PartitionKey(typeOfItem));
        CosmosPagedIterable<StateDocument> items = container.queryItems(spec, options, StateDocument.class);
        return StreamSupport.stream(items.spliterator(), false).toList();
    }

    @Override
    public void upsert(StateDocument document) {
        container.upsertItem(document, new PartitionKey(document.getTypeOfItem()), new CosmosItemRequestOptions());
    }

    @Override
    public void delete(String id, String typeOfItem) {
        try {
            container.deleteItem(id, new PartitionKey(typeOfItem), new CosmosItemRequestOptions());
        } catch (CosmosException e) {
            if (e.getStatusCode() != 404) {
                throw e;
            }
        }
    }
}
