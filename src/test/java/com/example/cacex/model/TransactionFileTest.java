package com.example.cacex.model;

import com.finbourne.lusid.model.TransactionTypeRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFileTest {

    @Test
    void setterUpdatesTransactionTypeRequest() {
        TransactionFile file = new TransactionFile();
        TransactionTypeRequest request = new TransactionTypeRequest();

        file.setSideDefinition(request);

        assertSame(request, file.getTransactionTypeRequest());
    }

    @Test
    void equalityReflectsAllFields() {
        TransactionTypeRequest request = new TransactionTypeRequest();
        TransactionFile first = build("Scope", "TypeA", "ClassA", "SourceA", 10, request);
        TransactionFile second = build("Scope", "TypeA", "ClassA", "SourceA", 10, request);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setTransactionSequence(11);
        assertNotEquals(first, second);
    }

    private TransactionFile build(String scope,
                                  String type,
                                  String transactionClass,
                                  String source,
                                  Integer sequence,
                                  TransactionTypeRequest request) {
        TransactionFile file = new TransactionFile();
        file.setScope(scope);
        file.setType(type);
        file.setTransactionClass(transactionClass);
        file.setSource(source);
        file.setTransactionSequence(sequence);
        file.setSideDefinition(request);
        return file;
    }
}
