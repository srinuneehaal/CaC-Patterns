package com.example.cacex.util;

import java.util.Arrays;
import java.util.Objects;

public final class CommandLineFlags {

    private CommandLineFlags() {
    }

    public static boolean hasFlag(String[] args, String flag) {
        if (args == null || args.length == 0 || flag == null) {
            return false;
        }
        return Arrays.stream(args)
                .filter(Objects::nonNull)
                .map(String::trim)
                .anyMatch(flag::equalsIgnoreCase);
    }
}
