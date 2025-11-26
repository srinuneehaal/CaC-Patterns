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

    public List<Account> getAccounts() {
        return accounts;
    }

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
