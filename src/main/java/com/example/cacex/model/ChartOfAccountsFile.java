package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.ChartOfAccountsRequest;

import java.util.Objects;

public class ChartOfAccountsFile {

    private String scope;
    private String chartOfAccountsCode;
    @JsonProperty("chartOfAccountsRequest")
    private ChartOfAccountsRequest chartOfAccountsRequest;

    /**
     * Returns the scope for the chart of accounts.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the chart of accounts.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the chart of accounts code.
     *
     * @return chart of accounts code
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chart of accounts code.
     *
     * @param chartOfAccountsCode chart of accounts code
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Returns the chart of accounts request payload.
     *
     * @return chart of accounts request
     */
    public ChartOfAccountsRequest getChartOfAccountsRequest() {
        return chartOfAccountsRequest;
    }

    /**
     * Sets the chart of accounts request payload.
     *
     * @param chartOfAccountsRequest chart of accounts request
     */
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
