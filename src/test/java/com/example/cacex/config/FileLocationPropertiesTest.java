package com.example.cacex.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileLocationPropertiesTest {

    @Test
    void usesPropertyValuesWhenEnvUnset() {
        FileLocationProperties props = new FileLocationProperties();
        props.setChangedFilesDir("changedfiles");
        props.setPlanDir("plan");
        props.setMasterPlanFile("masterplan.json");
        props.setEnvLookup(env -> null);

        assertEquals(Path.of("plan"), props.planDirPath());
        assertEquals(Path.of("plan").resolve("masterplan.json"), props.masterPlanPath());
        assertEquals(Path.of("changedfiles"), props.changedFilesRoot());
    }

    @Test
    void usesEnvOverridesWhenProvided() {
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir("plan-from-props");
        props.setMasterPlanFile("masterplan-props.json");
        props.setEnvLookup(env -> Map.of(
                        "PLAN_DIR", "env-plan",
                        "MASTER_PLAN_FILE", "env-master.json")
                .get(env));

        assertEquals(Path.of("env-plan"), props.planDirPath());
        assertEquals(Path.of("env-plan").resolve("env-master.json"), props.masterPlanPath());
    }

    @Test
    void blanksFallbackToProperties() {
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir("propPlan");
        props.setMasterPlanFile("propMaster.json");
        props.setEnvLookup(env -> "");

        assertEquals(Path.of("propPlan"), props.planDirPath());
        assertEquals(Path.of("propPlan").resolve("propMaster.json"), props.masterPlanPath());
    }

    @Test
    void gettersExposeConfiguredValues() {
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir("planDir");
        props.setMasterPlanFile("master.json");

        assertEquals("planDir", props.getPlanDir());
        assertEquals("master.json", props.getMasterPlanFile());
    }
}
