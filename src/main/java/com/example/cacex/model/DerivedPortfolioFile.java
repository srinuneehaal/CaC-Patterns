package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DerivedPortfolioFile {

    private String scope;

    @JsonProperty("createDerivedPortfolioRequestList")
    private List<CreateDerivedTransactionPortfolioRequest> derivedPortfolios = new ArrayList<>();

    /**
     * Returns the scope for derived portfolios.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for derived portfolios.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the derived portfolio requests.
     *
     * @return list of requests
     */
    public List<CreateDerivedTransactionPortfolioRequest> getDerivedPortfolios() {
        return derivedPortfolios;
    }

    /**
     * Sets the derived portfolio requests.
     *
     * @param derivedPortfolios list of requests
     */
    public void setDerivedPortfolios(List<CreateDerivedTransactionPortfolioRequest> derivedPortfolios) {
        this.derivedPortfolios = derivedPortfolios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DerivedPortfolioFile that = (DerivedPortfolioFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(derivedPortfolios, that.derivedPortfolios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, derivedPortfolios);
    }
}
