package com.example.cacex.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathUtilsTest {

    @Test
    void baseNameRemovesExtension() {
        assertEquals("file", PathUtils.baseName(Path.of("/tmp/file.txt")));
    }

    @Test
    void baseNameWithoutExtensionReturnsFullName() {
        assertEquals("file", PathUtils.baseName(Path.of("file")));
    }
}
