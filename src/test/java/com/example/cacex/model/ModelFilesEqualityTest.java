package com.example.cacex.model;

import com.finbourne.lusid.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void generalLedgerProfileFileEqualityMatchesAllFields() {
        GeneralLedgerProfileRequest request = new GeneralLedgerProfileRequest();
        request.setGeneralLedgerProfileCode("GLP");
        GeneralLedgerProfileFile first = new GeneralLedgerProfileFile();
        first.setScope("S");
        first.setChartOfAccountsCode("COA");
        first.setGeneralLedgerProfileCode("GLP");
        first.setGeneralLedgerProfileRequest(request);

        GeneralLedgerProfileFile second = new GeneralLedgerProfileFile();
        second.setScope("S");
        second.setChartOfAccountsCode("COA");
        second.setGeneralLedgerProfileCode("GLP");
        second.setGeneralLedgerProfileRequest(request);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setGeneralLedgerProfileCode("OTHER");
        assertNotEquals(first, second);
    }

    @Test
    void modelEqualityHandlesNullAndDifferentTypes() {
        assertNotEquals(null, new AborFile());
        assertNotEquals("other", new AborFile());
        assertNotEquals(null, new AborConfigurationFile());
        assertNotEquals("other", new AborConfigurationFile());
        assertNotEquals(new DerivedPortfolioFile(), new Object());
        assertNotEquals(null, new PortfolioGroupFile());
        assertNotEquals(null, new PostingRulesFile());
        assertNotEquals(null, new ChartOfAccountsFile());
        assertNotEquals(null, new AccountFile());
        assertNotEquals(null, new TransactionFile());
        assertEquals(new SideFile(), new SideFile());
        assertEquals(new TransactionFile(), new TransactionFile());
        assertEquals(new ChartOfAccountsFile(), new ChartOfAccountsFile());
        assertEquals(new PortfolioGroupFile(), new PortfolioGroupFile());
        assertEquals(new DerivedPortfolioFile(), new DerivedPortfolioFile());
        assertEquals(new PostingRulesFile(), new PostingRulesFile());
        assertEquals(new AborConfigurationFile(), new AborConfigurationFile());
        assertEquals(new AborFile(), new AborFile());
        assertEquals(new AccountFile(), new AccountFile());
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
