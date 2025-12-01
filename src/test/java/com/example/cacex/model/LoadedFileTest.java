package com.example.cacex.model;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LoadedFileTest {

    @Test
    void equalityIgnoresPathButHonorsPayload() {
        Object payload = new Object();
        LoadedFile first = new LoadedFile(FileCategory.SIDE, "key-1", Path.of("first"), payload);
        LoadedFile second = new LoadedFile(FileCategory.SIDE, "key-1", Path.of("second"), payload);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentPayloadBreaksEquality() {
        LoadedFile first = new LoadedFile(FileCategory.ABOR, "k", Path.of("a"), new Object());
        LoadedFile second = new LoadedFile(FileCategory.ABOR, "k", Path.of("a"), new Object());

        assertNotEquals(first, second);
    }
}
