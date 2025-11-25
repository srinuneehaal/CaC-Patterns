package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.ChartOfAccountsRequest;

import java.util.Objects;

public class ChartOfAccountsFile {

    private String scope;
    private String chartOfAccountsCode;
    @JsonProperty("chartOfAccountsRequest")
    private ChartOfAccountsRequest chartOfAccountsRequest;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public ChartOfAccountsRequest getChartOfAccountsRequest() {
        return chartOfAccountsRequest;
    }

    public void setChartOfAccountsRequest(ChartOfAccountsRequest chartOfAccountsRequest) {
        this.chartOfAccountsRequest = chartOfAccountsRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChartOfAccountsFile that = (ChartOfAccountsFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(chartOfAccountsCode, that.chartOfAccountsCode)
                && Objects.equals(chartOfAccountsRequest, that.chartOfAccountsRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, chartOfAccountsCode, chartOfAccountsRequest);
    }

    @Override
    public String toString() {
        return "ChartOfAccountsFile{" +
                "scope='" + scope + '\'' +
                ", chartOfAccountsCode='" + chartOfAccountsCode + '\'' +
                ", chartOfAccountsRequest=" + chartOfAccountsRequest +
                '}';
    }
}
