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
    private String planDir;
    private String masterPlanFile;
    private String sidesDirName = "sides";
    private String transactionsDirName = "transactions";
    private String derivedPortfoliosDirName = "derivedportfolios";
    private String portfolioGroupsDirName = "portfoliogroups";
    private String chartOfAccountsDirName = "coa";
    private String accountsDirName = "gla";
    private String postingRulesDirName = "postingrules";
    private String aborConfigurationsDirName = "aborconfigs";
    private String aborDirName = "abor";

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

    public String getSidesDirName() {
        return sidesDirName;
    }

    public void setSidesDirName(String sidesDirName) {
        this.sidesDirName = sidesDirName;
    }

    public String getTransactionsDirName() {
        return transactionsDirName;
    }

    public void setTransactionsDirName(String transactionsDirName) {
        this.transactionsDirName = transactionsDirName;
    }

    public String getDerivedPortfoliosDirName() {
        return derivedPortfoliosDirName;
    }

    public void setDerivedPortfoliosDirName(String derivedPortfoliosDirName) {
        this.derivedPortfoliosDirName = derivedPortfoliosDirName;
    }

    public String getPortfolioGroupsDirName() {
        return portfolioGroupsDirName;
    }

    public void setPortfolioGroupsDirName(String portfolioGroupsDirName) {
        this.portfolioGroupsDirName = portfolioGroupsDirName;
    }

    public String getChartOfAccountsDirName() {
        return chartOfAccountsDirName;
    }

    public void setChartOfAccountsDirName(String chartOfAccountsDirName) {
        this.chartOfAccountsDirName = chartOfAccountsDirName;
    }

    public String getAccountsDirName() {
        return accountsDirName;
    }

    public void setAccountsDirName(String accountsDirName) {
        this.accountsDirName = accountsDirName;
    }

    public String getPostingRulesDirName() {
        return postingRulesDirName;
    }

    public void setPostingRulesDirName(String postingRulesDirName) {
        this.postingRulesDirName = postingRulesDirName;
    }

    public String getAborConfigurationsDirName() {
        return aborConfigurationsDirName;
    }

    public void setAborConfigurationsDirName(String aborConfigurationsDirName) {
        this.aborConfigurationsDirName = aborConfigurationsDirName;
    }

    public String getAborDirName() {
        return aborDirName;
    }

    public void setAborDirName(String aborDirName) {
        this.aborDirName = aborDirName;
    }

    public Path changedFilesRoot() {
        return Path.of(changedFilesDir);
    }

    public Path stateFilesRoot() {
        return Path.of(stateFilesDir);
    }

    public Path planDirPath() {
        return Path.of(resolveFromEnv("PLAN_DIR", planDir));
    }

    public Path masterPlanPath() {
        String fileName = resolveFromEnv("MASTER_PLAN_FILE", masterPlanFile);
        return planDirPath().resolve(fileName);
    }

    private String resolveFromEnv(String envKey, String fallback) {
        String value = System.getenv(envKey);
        return value != null && !value.isBlank() ? value : fallback;
    }
}
