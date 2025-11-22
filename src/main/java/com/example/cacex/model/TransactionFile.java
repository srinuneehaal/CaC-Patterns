package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.TransactionTypeRequest;

import java.util.Objects;

public class TransactionFile {

    private String scope;
    @JsonProperty("transactionType")
    private String type;
    private String transactionClass;
    private String source;
    @JsonProperty("transactionTypeRequest")
    private TransactionTypeRequest transactionTypeRequest;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionClass() {
        return transactionClass;
    }

    public void setTransactionClass(String transactionClass) {
        this.transactionClass = transactionClass;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TransactionTypeRequest getTransactionTypeRequest() {
        return transactionTypeRequest;
    }

    public void setSideDefinition(TransactionTypeRequest sideDefinition) {
        this.transactionTypeRequest = sideDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionFile that = (TransactionFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(type, that.type)
                && Objects.equals(transactionClass, that.transactionClass)
                && Objects.equals(source, that.source)
                && Objects.equals(transactionTypeRequest, that.transactionTypeRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, type, transactionClass, source, transactionTypeRequest);
    }

    @Override
    public String toString() {
        return "TransactionFile{" +
                "scope='" + scope + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", transactionTypeRequest=" + transactionTypeRequest +
                '}';
    }
}
