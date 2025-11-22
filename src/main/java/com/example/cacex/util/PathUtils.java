package com.example.cacex.util;

import java.nio.file.Path;

public final class PathUtils {

    private PathUtils() {
    }

    public static String baseName(Path path) {
        String name = path.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }


}
