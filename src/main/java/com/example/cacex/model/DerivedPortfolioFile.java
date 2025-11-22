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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<CreateDerivedTransactionPortfolioRequest> getDerivedPortfolios() {
        return derivedPortfolios;
    }

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
