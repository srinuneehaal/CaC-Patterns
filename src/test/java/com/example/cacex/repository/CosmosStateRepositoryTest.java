package com.example.cacex.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.cacex.model.StateDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CosmosStateRepositoryTest {

    @Mock
    private CosmosContainer container;

    private CosmosStateRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CosmosStateRepository(container);
    }

    @Test
    void findReturnsStateDocumentWhenPresent() {
        StateDocument expected = new StateDocument();
        expected.setId("docId");
        expected.setTypeOfItem("type");
        CosmosItemResponse<StateDocument> response = mock(CosmosItemResponse.class);
        when(response.getItem()).thenReturn(expected);
        when(container.readItem(eq("docId"), eq(new PartitionKey("type")), eq(StateDocument.class)))
                .thenReturn(response);

        assertSame(expected, repository.find("docId", "type").orElseThrow());
        verify(container).readItem("docId", new PartitionKey("type"), StateDocument.class);
    }

    @Test
    void findReturnsEmptyWhenNotFound() {
        CosmosException cosmosException = mock(CosmosException.class);
        when(cosmosException.getStatusCode()).thenReturn(404);
        when(container.readItem(eq("missing"), eq(new PartitionKey("type")), eq(StateDocument.class)))
                .thenThrow(cosmosException);

        assertTrue(repository.find("missing", "type").isEmpty());
    }

    @Test
    void findPropagatesOtherCosmosExceptions() {
        CosmosException cosmosException = mock(CosmosException.class);
        when(cosmosException.getStatusCode()).thenReturn(500);
        when(container.readItem(eq("missing"), eq(new PartitionKey("type")), eq(StateDocument.class)))
                .thenThrow(cosmosException);

        assertThrows(CosmosException.class, () -> repository.find("missing", "type"));
    }

    @Test
    void listAppliesScopeFilterWhenProvided() {
        CosmosPagedIterable<StateDocument> iterable = mock(CosmosPagedIterable.class);
        StateDocument first = new StateDocument();
        first.setTypeOfItem("type");
        when(iterable.spliterator()).thenReturn(List.of(first).spliterator());

        ArgumentCaptor<SqlQuerySpec> queryCaptor = ArgumentCaptor.forClass(SqlQuerySpec.class);
        when(container.queryItems(queryCaptor.capture(), any(CosmosQueryRequestOptions.class), eq(StateDocument.class)))
                .thenReturn(iterable);

        List<StateDocument> documents = repository.list("type", "scope");

        assertEquals(List.of(first), documents);
        SqlQuerySpec spec = queryCaptor.getValue();
        assertTrue(spec.getQueryText().contains("AND c.scope = @scope"));
        assertEquals(2, spec.getParameters().size());
        SqlParameter scopeParameter = spec.getParameters().get(1);
        assertEquals("@scope", scopeParameter.getName());
        assertEquals("scope", scopeParameter.getValue(Object.class));
    }

    @Test
    void listSkipsScopeFilterWhenScopeIsBlank() {
        CosmosPagedIterable<StateDocument> iterable = mock(CosmosPagedIterable.class);
        StateDocument first = new StateDocument();
        first.setTypeOfItem("type");
        when(iterable.spliterator()).thenReturn(List.of(first).spliterator());

        ArgumentCaptor<SqlQuerySpec> queryCaptor = ArgumentCaptor.forClass(SqlQuerySpec.class);
        when(container.queryItems(queryCaptor.capture(), any(CosmosQueryRequestOptions.class), eq(StateDocument.class)))
                .thenReturn(iterable);

        List<StateDocument> documents = repository.list("type", null);

        assertEquals(List.of(first), documents);
        SqlQuerySpec spec = queryCaptor.getValue();
        assertFalse(spec.getQueryText().contains("@scope"));
        assertEquals(1, spec.getParameters().size());
    }

    @Test
    void upsertPersistsDocumentWithPartitionKey() {
        StateDocument document = new StateDocument();
        document.setTypeOfItem("type");

        repository.upsert(document);

        verify(container).upsertItem(eq(document), eq(new PartitionKey("type")), any(CosmosItemRequestOptions.class));
    }

    @Test
    void deleteRemovesDocument() {
        repository.delete("docId", "type");

        verify(container).deleteItem(eq("docId"), eq(new PartitionKey("type")), any(CosmosItemRequestOptions.class));
    }

    @Test
    void deleteIgnoresNotFound() {
        CosmosException cosmosException = mock(CosmosException.class);
        when(cosmosException.getStatusCode()).thenReturn(404);
        doThrow(cosmosException).when(container)
                .deleteItem(eq("docId"), eq(new PartitionKey("type")), any(CosmosItemRequestOptions.class));

        assertDoesNotThrow(() -> repository.delete("docId", "type"));
    }

    @Test
    void deletePropagatesOtherCosmosExceptions() {
        CosmosException cosmosException = mock(CosmosException.class);
        when(cosmosException.getStatusCode()).thenReturn(500);
        doThrow(cosmosException).when(container)
                .deleteItem(eq("docId"), eq(new PartitionKey("type")), any(CosmosItemRequestOptions.class));

        assertThrows(CosmosException.class, () -> repository.delete("docId", "type"));
    }
}
