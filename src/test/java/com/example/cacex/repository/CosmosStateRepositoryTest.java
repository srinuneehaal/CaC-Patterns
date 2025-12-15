package com.example.cacex.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.cacex.model.StateDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    void findReturnsDocumentWhenPresent() {
        StateDocument doc = createDoc("id-1");
        CosmosItemResponse<StateDocument> response = mock(CosmosItemResponse.class);
        when(response.getItem()).thenReturn(doc);
        when(container.readItem(eq("id-1"), any(PartitionKey.class), eq(StateDocument.class)))
                .thenReturn(response);

        Optional<StateDocument> result = repository.find("id-1", "SIDE");

        assertThat(result).contains(doc);
    }

    @Test
    void findHandlesMissingDocument() {
        CosmosException cosmosEx = mock(CosmosException.class);
        when(cosmosEx.getStatusCode()).thenReturn(404);
        when(container.readItem(eq("missing"), any(PartitionKey.class), eq(StateDocument.class)))
                .thenThrow(cosmosEx);

        Optional<StateDocument> result = repository.find("missing", "SIDE");

        assertThat(result).isEmpty();
    }

    @Test
    void listFiltersByScopeWhenProvided() {
        var docs = List.of(createDoc("d1"), createDoc("d2"));
        CosmosPagedIterable<StateDocument> paged = mock(CosmosPagedIterable.class);
        when(paged.spliterator()).thenReturn(docs.spliterator());
        when(container.queryItems(any(SqlQuerySpec.class), any(CosmosQueryRequestOptions.class), eq(StateDocument.class)))
                .thenReturn(paged);

        repository.list("SIDE", "S1");

        ArgumentCaptor<SqlQuerySpec> specCaptor = ArgumentCaptor.forClass(SqlQuerySpec.class);
        ArgumentCaptor<CosmosQueryRequestOptions> optionsCaptor = ArgumentCaptor.forClass(CosmosQueryRequestOptions.class);
        verify(container).queryItems(specCaptor.capture(), optionsCaptor.capture(), eq(StateDocument.class));
        SqlQuerySpec spec = specCaptor.getValue();
        assertThat(spec.getQueryText()).contains("AND c.scope = @scope");
    }

    @Test
    void listWithoutScopeOmitsScopeFilter() {
        var docs = List.of(createDoc("d1"));
        CosmosPagedIterable<StateDocument> paged = mock(CosmosPagedIterable.class);
        when(paged.spliterator()).thenReturn(docs.spliterator());
        when(container.queryItems(any(SqlQuerySpec.class), any(CosmosQueryRequestOptions.class), eq(StateDocument.class)))
                .thenReturn(paged);

        repository.list("SIDE", null);

        ArgumentCaptor<SqlQuerySpec> specCaptor = ArgumentCaptor.forClass(SqlQuerySpec.class);
        verify(container).queryItems(specCaptor.capture(), any(CosmosQueryRequestOptions.class), eq(StateDocument.class));
        assertThat(specCaptor.getValue().getQueryText()).doesNotContain("c.scope");
    }

    @Test
    void upsertDelegatesToContainer() {
        StateDocument doc = createDoc("d1");

        repository.upsert(doc);

        verify(container).upsertItem(eq(doc), any(PartitionKey.class), any(CosmosItemRequestOptions.class));
    }

    @Test
    void deleteIgnoresNotFound() {
        repository.delete("some", "SIDE");
        verify(container).deleteItem(eq("some"), any(PartitionKey.class), any(CosmosItemRequestOptions.class));
    }

    @Test
    void deleteRethrowsUnexpectedException() {
        CosmosException cosmosEx = mock(CosmosException.class);
        when(cosmosEx.getStatusCode()).thenReturn(500);
        doThrow(cosmosEx).when(container).deleteItem(eq("id"), any(PartitionKey.class), any(CosmosItemRequestOptions.class));

        assertThatThrownBy(() -> repository.delete("id", "SIDE")).isSameAs(cosmosEx);
    }

    private StateDocument createDoc(String id) {
        StateDocument doc = new StateDocument();
        doc.setId(id);
        doc.setTypeOfItem("SIDE");
        return doc;
    }
}
