package com.example.cacex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Centralised holder for filesystem locations so directory and file names
 * can be tweaked via application.properties without code changes.
 */
@Component
@ConfigurationProperties(prefix = "cacex.paths")
public class FileLocationProperties {

    private String changedFilesDir ;
    private String stateFilesDir;
    private String planDir ;
    private String masterPlanFile;

    public String getChangedFilesDir() {
        return changedFilesDir;
    }

    public void setChangedFilesDir(String changedFilesDir) {
        this.changedFilesDir = changedFilesDir;
    }

    public String getStateFilesDir() {
        return stateFilesDir;
    }

    public void setStateFilesDir(String stateFilesDir) {
        this.stateFilesDir = stateFilesDir;
    }

    public String getPlanDir() {
        return planDir;
    }

    public void setPlanDir(String planDir) {
        this.planDir = planDir;
    }

    public String getMasterPlanFile() {
        return masterPlanFile;
    }

    public void setMasterPlanFile(String masterPlanFile) {
        this.masterPlanFile = masterPlanFile;
    }

    public Path changedFilesRoot() {
        return Path.of(changedFilesDir);
    }

    public Path stateFilesRoot() {
        return Path.of(stateFilesDir);
    }

    public Path planDirPath() {
        return Path.of(planDir);
    }

    public Path masterPlanPath() {
        return planDirPath().resolve(masterPlanFile);
    }
}
