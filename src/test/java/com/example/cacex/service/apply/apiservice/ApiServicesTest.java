package com.example.cacex.service.apply.apiservice;

import com.example.cacex.model.SideFile;
import com.example.cacex.model.TransactionFile;
import com.finbourne.lusid.model.AborConfigurationRequest;
import com.finbourne.lusid.model.AborRequest;
import com.finbourne.lusid.model.Account;
import com.finbourne.lusid.model.ChartOfAccountsRequest;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import com.finbourne.lusid.model.PostingModuleRequest;
import org.junit.jupiter.api.Test;

class ApiServicesTest {

    @Test
    void transactionTypeApiServiceCoversAllActions() {
        TransactionTypeApiService service = new TransactionTypeApiService();
        service.create("S", "K", new TransactionFile());
        service.update("S", "K", new TransactionFile());
        service.delete("S", "K", new TransactionFile());
    }

    @Test
    void sideDefinitionApiServiceCoversAllActions() {
        SideDefinitionApiService service = new SideDefinitionApiService();
        service.create("S", "K", new SideFile());
        service.update("S", "K", new SideFile());
        service.delete("S", "K", new SideFile());
    }

    @Test
    void postingModuleApiServiceCoversAllActions() {
        PostingModuleApiService service = new PostingModuleApiService();
        service.create("S", "K", new PostingModuleRequest());
        service.update("S", "K", new PostingModuleRequest());
        service.delete("S", "K", new PostingModuleRequest());
    }

    @Test
    void portfolioGroupApiServiceCoversAllActions() {
        PortfolioGroupApiService service = new PortfolioGroupApiService();
        service.create("S", "K", new CreatePortfolioGroupRequest());
        service.update("S", "K", new CreatePortfolioGroupRequest());
        service.delete("S", "K", new CreatePortfolioGroupRequest());
    }

    @Test
    void derivedPortfolioApiServiceCoversAllActions() {
        DerivedPortfolioApiService service = new DerivedPortfolioApiService();
        service.create("S", "K", new CreateDerivedTransactionPortfolioRequest());
        service.update("S", "K", new CreateDerivedTransactionPortfolioRequest());
        service.delete("S", "K", new CreateDerivedTransactionPortfolioRequest());
    }

    @Test
    void chartOfAccountsApiServiceCoversAllActions() {
        ChartOfAccountsApiService service = new ChartOfAccountsApiService();
        service.create("S", "K", new ChartOfAccountsRequest());
        service.update("S", "K", new ChartOfAccountsRequest());
        service.delete("S", "K", new ChartOfAccountsRequest());
    }

    @Test
    void accountApiServiceCoversAllActions() {
        AccountApiService service = new AccountApiService();
        service.create("S", "K", new Account());
        service.update("S", "K", new Account());
        service.delete("S", "K", new Account());
    }

    @Test
    void aborAndConfigApiServicesCoverAllActions() {
        AborApiService aborApi = new AborApiService();
        aborApi.create("S", "K", new AborRequest());
        aborApi.update("S", "K", new AborRequest());
        aborApi.delete("S", "K", new AborRequest());

        AborConfigurationApiService cfgApi = new AborConfigurationApiService();
        cfgApi.create("S", "K", new AborConfigurationRequest());
        cfgApi.update("S", "K", new AborConfigurationRequest());
        cfgApi.delete("S", "K", new AborConfigurationRequest());
    }
}
