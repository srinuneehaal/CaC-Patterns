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
    private Integer transactionSequence;
    @JsonProperty("transactionTypeRequest")
    private TransactionTypeRequest transactionTypeRequest;

    /**
     * Returns the scope for the transaction type.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the transaction type.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the transaction type code.
     *
     * @return type code
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the transaction type code.
     *
     * @param type type code
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the transaction class.
     *
     * @return transaction class
     */
    public String getTransactionClass() {
        return transactionClass;
    }

    /**
     * Sets the transaction class.
     *
     * @param transactionClass transaction class
     */
    public void setTransactionClass(String transactionClass) {
        this.transactionClass = transactionClass;
    }

    /**
     * Returns the transaction source identifier.
     *
     * @return source identifier
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the transaction source identifier.
     *
     * @param source source identifier
     */
    public void setSource(String source) {
        this.source = source;
    }
    /**
     * Returns the transaction sequence weight.
     *
     * @return transaction sequence or null
     */
    public Integer getTransactionSequence() {
        return transactionSequence;
    }

    /**
     * Sets the transaction sequence weight.
     *
     * @param transactionSequence transaction sequence
     */
    public void setTransactionSequence(Integer transactionSequence) {
        this.transactionSequence = transactionSequence;
    }
    /**
     * Returns the transaction type request payload.
     *
     * @return transaction type request
     */
    public TransactionTypeRequest getTransactionTypeRequest() {
        return transactionTypeRequest;
    }

    /**
     * Sets the transaction type request payload.
     *
     * @param sideDefinition transaction type request
     */
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
                && Objects.equals(transactionSequence, that.transactionSequence)
                && Objects.equals(transactionTypeRequest, that.transactionTypeRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, type, transactionClass, source, transactionSequence, transactionTypeRequest);
    }

    @Override
    public String toString() {
        return "TransactionFile{" +
                "scope='" + scope + '\'' +
                ", type='" + type + '\'' +
                ", transactionClass='" + transactionClass + '\'' +
                ", source='" + source + '\'' +
                ", transactionSequence=" + transactionSequence +
                ", transactionTypeRequest=" + transactionTypeRequest +
                '}';
    }
}
