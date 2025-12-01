package com.example.cacex.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLineFlagsTest {

    @Test
    void detectsFlagPresence() {
        assertTrue(CommandLineFlags.hasFlag(new String[] {"--plan"}, "--plan"));
    }

    @Test
    void returnsFalseWhenFlagMissing() {
        assertFalse(CommandLineFlags.hasFlag(new String[] {"--other"}, "--plan"));
    }

    @Test
    void handlesNullArgs() {
        assertFalse(CommandLineFlags.hasFlag(null, "--plan"));
    }
}
