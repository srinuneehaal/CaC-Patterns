package com.example.cacex.model;

import com.finbourne.lusid.model.GeneralLedgerProfileRequest;

import java.util.Objects;

public class GeneralLedgerProfileFile {

    private String scope;
    private String chartOfAccountsCode;
    private String generalLedgerProfileCode;
    private GeneralLedgerProfileRequest generalLedgerProfileRequest;

    /**
     * Returns the scope for the file.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the file.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the associated chart of accounts code.
     *
     * @return chart of accounts code
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chart of accounts code.
     *
     * @param chartOfAccountsCode chart code
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Returns the general ledger profile code contained in this file.
     *
     * @return profile code
     */
    public String getGeneralLedgerProfileCode() {
        return generalLedgerProfileCode;
    }

    /**
     * Sets the general ledger profile code.
     *
     * @param generalLedgerProfileCode profile code
     */
    public void setGeneralLedgerProfileCode(String generalLedgerProfileCode) {
        this.generalLedgerProfileCode = generalLedgerProfileCode;
    }

    /**
     * Returns the general ledger profile request payload.
     *
     * @return profile request
     */
    public GeneralLedgerProfileRequest getGeneralLedgerProfileRequest() {
        return generalLedgerProfileRequest;
    }

    /**
     * Sets the general ledger profile request payload.
     *
     * @param generalLedgerProfileRequest profile request
     */
    public void setGeneralLedgerProfileRequest(GeneralLedgerProfileRequest generalLedgerProfileRequest) {
        this.generalLedgerProfileRequest = generalLedgerProfileRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneralLedgerProfileFile that = (GeneralLedgerProfileFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(chartOfAccountsCode, that.chartOfAccountsCode)
                && Objects.equals(generalLedgerProfileCode, that.generalLedgerProfileCode)
                && Objects.equals(generalLedgerProfileRequest, that.generalLedgerProfileRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, chartOfAccountsCode, generalLedgerProfileCode, generalLedgerProfileRequest);
    }

    @Override
    public String toString() {
        return "GeneralLedgerProfileFile{" +
                "scope='" + scope + '\'' +
                ", chartOfAccountsCode='" + chartOfAccountsCode + '\'' +
                ", generalLedgerProfileCode='" + generalLedgerProfileCode + '\'' +
                ", generalLedgerProfileRequest=" + generalLedgerProfileRequest +
                '}';
    }
}
