package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountFile {

    private String scope;
    private String chartOfAccountsCode;

    @JsonProperty("glAccounts")
    private List<Account> accounts = new ArrayList<>();

    /**
     * Returns the scope for the account file.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the account file.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the chart of accounts code associated with the accounts.
     *
     * @return chart of accounts code
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chart of accounts code associated with the accounts.
     *
     * @param chartOfAccountsCode chart of accounts code
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Returns the list of GL accounts in the file.
     *
     * @return list of accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Sets the list of GL accounts in the file.
     *
     * @param accounts list of accounts
     */
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountFile that = (AccountFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(chartOfAccountsCode, that.chartOfAccountsCode)
                && Objects.equals(accounts, that.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, chartOfAccountsCode, accounts);
    }
}
