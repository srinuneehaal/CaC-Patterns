package com.example.cacex.config;

@FunctionalInterface
public interface EnvironmentLookup {

    String lookup(String key);
}
