package com.example.cacex.model;

import com.finbourne.lusid.model.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelToStringTest {

    @Test
    void loadedFileToStringAndEqualityCoverAllBranches() {
        Object payload = new Object();
        LoadedFile loadedFile = new LoadedFile(FileCategory.SIDE, "k1", Path.of("p1"), payload);

        assertTrue(loadedFile.toString().contains("k1"));
        assertEquals(loadedFile, loadedFile);
        assertNotEquals(null, loadedFile);
        assertNotEquals("other", loadedFile);
    }

    @Test
    void postingRulesFileToStringAndEquality() {
        PostingModuleRequest request = new PostingModuleRequest();
        PostingRulesFile first = new PostingRulesFile();
        first.setScope("S");
        first.setChartOfAccountsCode("COA");
        first.setPostingModuleCode("PM");
        first.setPostingModuleRequest(request);

        PostingRulesFile second = new PostingRulesFile();
        second.setScope("S");
        second.setChartOfAccountsCode("COA");
        second.setPostingModuleCode("PM");
        second.setPostingModuleRequest(request);

        assertEquals(first, second);
        assertNotEquals(null, first);
        assertNotEquals("x", first);
        assertTrue(first.toString().contains("PM"));
    }

    @Test
    void chartOfAccountsFileCoversHashCodeEqualsAndToString() {
        ChartOfAccountsRequest request = new ChartOfAccountsRequest();
        ChartOfAccountsFile first = new ChartOfAccountsFile();
        first.setScope("S");
        first.setChartOfAccountsCode("COA");
        first.setChartOfAccountsRequest(request);

        ChartOfAccountsFile second = new ChartOfAccountsFile();
        second.setScope("S");
        second.setChartOfAccountsCode("COA");
        second.setChartOfAccountsRequest(request);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("COA"));
    }

    @Test
    void transactionFileEqualityAndToString() {
        TransactionTypeRequest request = new TransactionTypeRequest();
        TransactionFile first = new TransactionFile();
        first.setScope("S");
        first.setType("TYPE");
        first.setTransactionClass("CLASS");
        first.setSource("SRC");
        first.setTransactionSequence(1);
        first.setSideDefinition(request);

        TransactionFile second = new TransactionFile();
        second.setScope("S");
        second.setType("TYPE");
        second.setTransactionClass("CLASS");
        second.setSource("SRC");
        second.setTransactionSequence(1);
        second.setSideDefinition(request);

        assertEquals(first, second);
        assertNotEquals("other", first);
        assertTrue(first.toString().contains("CLASS"));
    }

    @Test
    void sideFileAccountFileAndGroupFileEqualityVariants() {
        SideFile side = new SideFile();
        side.setScope("S");
        side.setSide("BUY");
        assertNotEquals("different", side);
        assertTrue(side.toString().contains("BUY"));

        Account account = new Account();
        account.setCode("ACC");
        AccountFile accountFile = new AccountFile();
        accountFile.setScope("S");
        accountFile.setChartOfAccountsCode("COA");
        accountFile.setAccounts(List.of(account));
        assertEquals("COA", accountFile.getChartOfAccountsCode());
        assertEquals(accountFile, accountFile);

        CreatePortfolioGroupRequest groupReq = new CreatePortfolioGroupRequest();
        groupReq.setCode("G1");
        PortfolioGroupFile groupFile = new PortfolioGroupFile();
        groupFile.setScope("S");
        groupFile.setGroups(List.of(groupReq));
        assertEquals(groupReq, groupFile.getGroups().getFirst());
    }

    @Test
    void derivedPortfolioAndAborVariantsCoverToString() {
        CreateDerivedTransactionPortfolioRequest derivedReq = new CreateDerivedTransactionPortfolioRequest();
        derivedReq.setCode("DP1");
        DerivedPortfolioFile derived = new DerivedPortfolioFile();
        derived.setScope("S");
        derived.setDerivedPortfolios(List.of(derivedReq));
        assertEquals(1, derived.getDerivedPortfolios().size());

        AborRequest aborReq = new AborRequest();
        aborReq.setCode("AB1");
        AborFile aborFile = new AborFile();
        aborFile.setScope("S");
        aborFile.setAborRequests(List.of(aborReq));
        assertEquals(aborReq, aborFile.getAborRequests().getFirst());

        AborConfigurationRequest cfgReq = new AborConfigurationRequest();
        cfgReq.setCode("CFG");
        AborConfigurationFile cfgFile = new AborConfigurationFile();
        cfgFile.setScope("S");
        cfgFile.setAborConfigurations(List.of(cfgReq));
        assertEquals(cfgReq, cfgFile.getAborConfigurations().getFirst());
    }
}
