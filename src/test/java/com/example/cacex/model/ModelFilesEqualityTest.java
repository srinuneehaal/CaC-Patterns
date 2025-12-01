package com.example.cacex.model;

import com.finbourne.lusid.model.AborConfigurationRequest;
import com.finbourne.lusid.model.AborRequest;
import com.finbourne.lusid.model.Account;
import com.finbourne.lusid.model.ChartOfAccountsRequest;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import com.finbourne.lusid.model.PostingModuleRequest;
import com.finbourne.lusid.model.SideDefinitionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelFilesEqualityTest {

    @Test
    void accountFileUsesScopeChartAndAccountsForEquality() {
        AccountFile first = new AccountFile();
        first.setScope("Scope");
        first.setChartOfAccountsCode("COA");
        List<Account> accounts = List.of(new Account());
        first.setAccounts(accounts);

        AccountFile second = new AccountFile();
        second.setScope("Scope");
        second.setChartOfAccountsCode("COA");
        second.setAccounts(accounts);

        assertNotNull(first.getAccounts());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setChartOfAccountsCode("Other");
        assertNotEquals(first, second);
    }

    @Test
    void chartOfAccountsFileEqualityMatchesFields() {
        ChartOfAccountsFile first = new ChartOfAccountsFile();
        first.setScope("Scope");
        first.setChartOfAccountsCode("COA");
        ChartOfAccountsRequest request = new ChartOfAccountsRequest();
        first.setChartOfAccountsRequest(request);

        ChartOfAccountsFile second = new ChartOfAccountsFile();
        second.setScope("Scope");
        second.setChartOfAccountsCode("COA");
        second.setChartOfAccountsRequest(request);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setScope("Other");
        assertNotEquals(first, second);
    }

    @Test
    void postingRulesFileEqualityMatchesAllFields() {
        PostingRulesFile first = new PostingRulesFile();
        first.setScope("Scope");
        first.setChartOfAccountsCode("COA");
        first.setPostingModuleCode("PM");
        PostingModuleRequest request = new PostingModuleRequest();
        first.setPostingModuleRequest(request);

        PostingRulesFile second = new PostingRulesFile();
        second.setScope("Scope");
        second.setChartOfAccountsCode("COA");
        second.setPostingModuleCode("PM");
        second.setPostingModuleRequest(request);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setPostingModuleCode("Other");
        assertNotEquals(first, second);
    }

    @Test
    void sideFileEqualityReflectsAllFields() {
        SideDefinitionRequest definition = new SideDefinitionRequest();
        SideFile first = new SideFile();
        first.setScope("Scope");
        first.setSide("SideA");
        first.setSideDefinition(definition);

        SideFile second = new SideFile();
        second.setScope("Scope");
        second.setSide("SideA");
        second.setSideDefinition(definition);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setSide("Different");
        assertNotEquals(first, second);
    }

    @Test
    void derivedPortfolioFileInitializesListAndUsesScopeAndList() {
        DerivedPortfolioFile first = new DerivedPortfolioFile();
        List<CreateDerivedTransactionPortfolioRequest> portfolios = List.of(new CreateDerivedTransactionPortfolioRequest());
        first.setScope("Scope");
        first.setDerivedPortfolios(portfolios);

        DerivedPortfolioFile second = new DerivedPortfolioFile();
        second.setScope("Scope");
        second.setDerivedPortfolios(portfolios);

        assertNotNull(first.getDerivedPortfolios());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setScope("Other");
        assertNotEquals(first, second);
    }

    @Test
    void portfolioGroupFileInitializesListAndUsesScopeAndList() {
        PortfolioGroupFile first = new PortfolioGroupFile();
        List<CreatePortfolioGroupRequest> groups = List.of(new CreatePortfolioGroupRequest());
        first.setScope("Scope");
        first.setGroups(groups);

        PortfolioGroupFile second = new PortfolioGroupFile();
        second.setScope("Scope");
        second.setGroups(groups);

        assertNotNull(first.getGroups());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setScope("Other");
        assertNotEquals(first, second);
    }

    @Test
    void aborConfigurationFileInitializesListAndUsesScopeAndList() {
        AborConfigurationFile first = new AborConfigurationFile();
        List<AborConfigurationRequest> configs = List.of(new AborConfigurationRequest());
        first.setScope("Scope");
        first.setAborConfigurations(configs);

        AborConfigurationFile second = new AborConfigurationFile();
        second.setScope("Scope");
        second.setAborConfigurations(configs);

        assertNotNull(first.getAborConfigurations());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setScope("Other");
        assertNotEquals(first, second);
    }

    @Test
    void aborFileInitializesListAndUsesScopeAndList() {
        AborFile first = new AborFile();
        List<AborRequest> abors = List.of(new AborRequest());
        first.setScope("Scope");
        first.setAborRequests(abors);

        AborFile second = new AborFile();
        second.setScope("Scope");
        second.setAborRequests(abors);

        assertNotNull(first.getAborRequests());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setScope("Other");
        assertNotEquals(first, second);
    }

    @Test
    void modelEqualityHandlesNullAndDifferentTypes() {
        assertNotEquals(new AborFile(), null);
        assertNotEquals(new AborFile(), "other");
        assertNotEquals(new AborConfigurationFile(), null);
        assertNotEquals(new AborConfigurationFile(), "other");
        assertNotEquals(new DerivedPortfolioFile(), new Object());
        assertNotEquals(new PortfolioGroupFile(), null);
        assertNotEquals(new PostingRulesFile(), null);
        assertNotEquals(new ChartOfAccountsFile(), null);
        assertNotEquals(new AccountFile(), null);
        assertNotEquals(new TransactionFile(), null);
        assertEquals(new SideFile(), new SideFile());
        assertTrue(new TransactionFile().equals(new TransactionFile()));
        assertTrue(new ChartOfAccountsFile().equals(new ChartOfAccountsFile()));
        assertTrue(new PortfolioGroupFile().equals(new PortfolioGroupFile()));
        assertTrue(new DerivedPortfolioFile().equals(new DerivedPortfolioFile()));
        assertTrue(new PostingRulesFile().equals(new PostingRulesFile()));
        assertTrue(new AborConfigurationFile().equals(new AborConfigurationFile()));
        assertTrue(new AborFile().equals(new AborFile()));
        assertTrue(new AccountFile().equals(new AccountFile()));
    }

    @Test
    void modelsAreEqualToThemselves() {
        SideFile side = new SideFile();
        assertEquals(side, side);

        TransactionFile txn = new TransactionFile();
        assertEquals(txn, txn);

        ChartOfAccountsFile coa = new ChartOfAccountsFile();
        assertEquals(coa, coa);

        PortfolioGroupFile pg = new PortfolioGroupFile();
        assertEquals(pg, pg);

        DerivedPortfolioFile dp = new DerivedPortfolioFile();
        assertEquals(dp, dp);

        PostingRulesFile pr = new PostingRulesFile();
        assertEquals(pr, pr);

        AborConfigurationFile cfg = new AborConfigurationFile();
        assertEquals(cfg, cfg);

        AborFile abor = new AborFile();
        assertEquals(abor, abor);

        AccountFile accountFile = new AccountFile();
        assertEquals(accountFile, accountFile);
    }
}
