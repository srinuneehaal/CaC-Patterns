package com.example.cacex.service.plan;

import com.example.cacex.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonModelMapperTest {

    private final JsonModelMapper mapper = new JsonModelMapper();

    @Test
    void parsesSideFile() throws IOException {
        Path temp = Files.createTempFile("side", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "side": "BUY",
                  "sideDefinitionRequest": { "units": 1.0 }
                }
                """);
        SideFile file = mapper.read(temp, SideFile.class);
        assertEquals("BUY", file.getSide());
        assertEquals("SCP", file.getScope());
        assertNotNull(file.getSideDefinition());
    }

    @Test
    void parsesTransactionFile() throws IOException {
        Path temp = Files.createTempFile("txn", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "transactionType": "TYPE",
                  "transactionClass": "CLS",
                  "source": "SRC",
                  "transactionSequence": 7,
                  "transactionTypeRequest": { "description": "d" }
                }
                """);
        TransactionFile file = mapper.read(temp, TransactionFile.class);
        assertEquals("TYPE", file.getType());
        assertEquals(7, file.getTransactionSequence());
        assertNotNull(file.getTransactionTypeRequest());
    }

    @Test
    void parsesAccountFile() throws IOException {
        Path temp = Files.createTempFile("acct", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "chartOfAccountsCode": "COA",
                  "glAccounts": [ { "code": "ACC1" } ]
                }
                """);
        AccountFile file = mapper.read(temp, AccountFile.class);
        assertEquals("COA", file.getChartOfAccountsCode());
        assertEquals(1, file.getAccounts().size());
    }

    @Test
    void parsesDerivedPortfolioFile() throws IOException {
        Path temp = Files.createTempFile("derived", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "createDerivedPortfolioRequestList": [
                    { "code": "DP1", "displayName": "name" }
                  ]
                }
                """);
        DerivedPortfolioFile file = mapper.read(temp, DerivedPortfolioFile.class);
        assertEquals(1, file.getDerivedPortfolios().size());
        assertEquals("DP1", file.getDerivedPortfolios().get(0).getCode());
    }

    @Test
    void parsesPortfolioGroupFile() throws IOException {
        Path temp = Files.createTempFile("group", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "createPortfolioGroupRequestList": [
                    { "code": "G1", "displayName": "Group" }
                  ]
                }
                """);
        PortfolioGroupFile file = mapper.read(temp, PortfolioGroupFile.class);
        assertEquals(1, file.getGroups().size());
        assertEquals("G1", file.getGroups().get(0).getCode());
    }

    @Test
    void parsesAborConfigurationFile() throws IOException {
        Path temp = Files.createTempFile("aborcfg", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "aborConfigurationRequestList": [
                    { "code": "CFG1", "displayName": "cfg" }
                  ]
                }
                """);
        AborConfigurationFile file = mapper.read(temp, AborConfigurationFile.class);
        assertEquals(1, file.getAborConfigurations().size());
        assertEquals("CFG1", file.getAborConfigurations().get(0).getCode());
    }

    @Test
    void parsesAborFile() throws IOException {
        Path temp = Files.createTempFile("abor", ".json");
        Files.writeString(temp, """
                {
                  "scope": "SCP",
                  "aborRequestList": [
                    { "code": "AB1", "displayName": "abor" }
                  ]
                }
                """);
        AborFile file = mapper.read(temp, AborFile.class);
        assertEquals(1, file.getAborRequests().size());
        assertEquals("AB1", file.getAborRequests().get(0).getCode());
    }

    @Test
    void missingCollectionsAreHandledGracefully() throws IOException {
        Path sidePath = Files.createTempFile("side-missing", ".json");
        Files.writeString(sidePath, """
                { "scope": "S", "side": "SELL" }
                """);
        Path txnPath = Files.createTempFile("txn-missing", ".json");
        Files.writeString(txnPath, """
                { "scope": "S", "transactionType": "TYPE" }
                """);
        Path derivedPath = Files.createTempFile("derived-missing", ".json");
        Files.writeString(derivedPath, "{ \"scope\": \"S\" }");
        Path groupPath = Files.createTempFile("group-missing", ".json");
        Files.writeString(groupPath, "{ \"scope\": \"S\" }");
        Path coaPath = Files.createTempFile("coa-missing", ".json");
        Files.writeString(coaPath, "{ \"scope\": \"S\" }");
        Path accountPath = Files.createTempFile("account-missing", ".json");
        Files.writeString(accountPath, "{ \"scope\": \"S\" }");
        Path cfgPath = Files.createTempFile("cfg-missing", ".json");
        Files.writeString(cfgPath, "{ \"scope\": \"S\" }");
        Path aborPath = Files.createTempFile("abor-missing", ".json");
        Files.writeString(aborPath, "{ \"scope\": \"S\" }");

        assertNull(mapper.read(sidePath, SideFile.class).getSideDefinition());
        assertNull(mapper.read(txnPath, TransactionFile.class).getTransactionTypeRequest());
        assertTrue(mapper.read(derivedPath, DerivedPortfolioFile.class).getDerivedPortfolios().isEmpty());
        assertTrue(mapper.read(groupPath, PortfolioGroupFile.class).getGroups().isEmpty());
        assertNull(mapper.read(coaPath, ChartOfAccountsFile.class).getChartOfAccountsRequest());
        assertTrue(mapper.read(accountPath, AccountFile.class).getAccounts().isEmpty());
        assertTrue(mapper.read(cfgPath, AborConfigurationFile.class).getAborConfigurations().isEmpty());
        assertTrue(mapper.read(aborPath, AborFile.class).getAborRequests().isEmpty());
    }

    @Test
    void fallsBackToGenericMapping() throws IOException {
        Path planItemPath = Files.createTempFile("planitem", ".json");
        Files.writeString(planItemPath, """
                {
                  "action": "NEW",
                  "fileCategory": "SIDE",
                  "scope": "S",
                  "key": "K"
                }
                """);

        PlanItem planItem = mapper.read(planItemPath, PlanItem.class);
        assertEquals("K", planItem.getKey());
    }
}