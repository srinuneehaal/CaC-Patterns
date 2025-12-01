package com.example.cacex.service.plan;

import com.example.cacex.config.FileLocationProperties;
import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.MasterPlan;
import com.example.cacex.model.PlanItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesPlanToConfiguredLocation() throws Exception {
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir(tempDir.toString());
        props.setMasterPlanFile("plan.json");

        PlanWriter writer = new PlanWriter(props);
        MasterPlan plan = new MasterPlan();
        plan.addItem(new PlanItem(Action.NEW, FileCategory.SIDE, "S", "K", "path", null));

        Path out = writer.write(plan);

        assertTrue(Files.exists(out));
        String content = Files.readString(out);
        assertTrue(content.contains("\"items\""));
        assertTrue(content.contains("\"SIDE\""));
    }

    @Test
    void handlesWritingWhenParentIsNull() throws Exception {
        FileLocationProperties props = new FileLocationProperties();
        props.setPlanDir("");
        props.setMasterPlanFile("standalone.json");
        props.setEnvLookup(__ -> null);

        PlanWriter writer = new PlanWriter(props);
        MasterPlan plan = new MasterPlan();

        Path out = writer.write(plan);

        assertTrue(Files.exists(out));
    }

    @Test
    void writeThrowsIllegalStateExceptionOnIoFailure() throws Exception {
        Path tempFile = Files.createTempFile("plan-writer", ".tmp");
        FileLocationProperties props = new FileLocationProperties();
        // planDir points to a file, so parent of master plan path is the file path and directory creation will fail
        props.setPlanDir(tempFile.toString());
        props.setMasterPlanFile("plan.json");
        props.setEnvLookup(__ -> null);

        PlanWriter writer = new PlanWriter(props);

        assertThrows(IllegalStateException.class, () -> writer.write(new MasterPlan()));
    }
}
