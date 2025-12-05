package com.example.cacex.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileCategoryTest {

    @Test
    void valuesContainGeneralLedgerProfile() {
        assertTrue(Arrays.asList(FileCategory.values()).contains(FileCategory.GENERAL_LEDGER_PROFILE));
    }

    @Test
    void valueOfResolvesEnumNames() {
        assertEquals(FileCategory.GENERAL_LEDGER_PROFILE, FileCategory.valueOf("GENERAL_LEDGER_PROFILE"));
    }
}
