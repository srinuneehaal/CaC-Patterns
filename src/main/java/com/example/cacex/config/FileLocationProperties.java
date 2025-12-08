package com.example.cacex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Centralised holder for filesystem locations so directory and file names
 * can be tweaked via application.properties without code changes.
 */
@Component
@ConfigurationProperties(prefix = "cacex.paths")
public class FileLocationProperties {

    private String changedFilesDir ;
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
    private String generalLedgerProfilesDirName = "glprofile";
    private EnvironmentLookup envLookup = System::getenv;

    /**
     * Returns the configured changed files directory.
     *
     * @return changed files directory
     */
    public String getChangedFilesDir() {
        return changedFilesDir;
    }

    /**
     * Sets the root directory containing changed files.
     *
     * @param changedFilesDir directory name or path
     */
    public void setChangedFilesDir(String changedFilesDir) {
        this.changedFilesDir = changedFilesDir;
    }

    /**
     * Returns the configured plan directory.
     *
     * @return plan directory
     */
    public String getPlanDir() {
        return planDir;
    }

    /**
     * Sets the directory where plan files are written.
     *
     * @param planDir directory name or path
     */
    public void setPlanDir(String planDir) {
        this.planDir = planDir;
    }

    /**
     * Returns the master plan filename.
     *
     * @return master plan filename
     */
    public String getMasterPlanFile() {
        return masterPlanFile;
    }

    /**
     * Sets the master plan filename.
     *
     * @param masterPlanFile file name for the master plan
     */
    public void setMasterPlanFile(String masterPlanFile) {
        this.masterPlanFile = masterPlanFile;
    }

    /**
     * Returns the directory name for side files.
     *
     * @return sides directory name
     */
    public String getSidesDirName() {
        return sidesDirName;
    }

    /**
     * Sets the directory name for side files.
     *
     * @param sidesDirName directory name
     */
    public void setSidesDirName(String sidesDirName) {
        this.sidesDirName = sidesDirName;
    }

    /**
     * Returns the directory name for transaction files.
     *
     * @return transactions directory name
     */
    public String getTransactionsDirName() {
        return transactionsDirName;
    }

    /**
     * Sets the directory name for transaction files.
     *
     * @param transactionsDirName directory name
     */
    public void setTransactionsDirName(String transactionsDirName) {
        this.transactionsDirName = transactionsDirName;
    }

    /**
     * Returns the directory name for derived portfolio files.
     *
     * @return derived portfolio directory name
     */
    public String getDerivedPortfoliosDirName() {
        return derivedPortfoliosDirName;
    }

    /**
     * Sets the directory name for derived portfolio files.
     *
     * @param derivedPortfoliosDirName directory name
     */
    public void setDerivedPortfoliosDirName(String derivedPortfoliosDirName) {
        this.derivedPortfoliosDirName = derivedPortfoliosDirName;
    }

    /**
     * Returns the directory name for portfolio group files.
     *
     * @return portfolio group directory name
     */
    public String getPortfolioGroupsDirName() {
        return portfolioGroupsDirName;
    }

    /**
     * Sets the directory name for portfolio group files.
     *
     * @param portfolioGroupsDirName directory name
     */
    public void setPortfolioGroupsDirName(String portfolioGroupsDirName) {
        this.portfolioGroupsDirName = portfolioGroupsDirName;
    }

    /**
     * Returns the directory name for chart of accounts files.
     *
     * @return chart of accounts directory name
     */
    public String getChartOfAccountsDirName() {
        return chartOfAccountsDirName;
    }

    /**
     * Sets the directory name for chart of accounts files.
     *
     * @param chartOfAccountsDirName directory name
     */
    public void setChartOfAccountsDirName(String chartOfAccountsDirName) {
        this.chartOfAccountsDirName = chartOfAccountsDirName;
    }

    /**
     * Returns the directory name for account files.
     *
     * @return accounts directory name
     */
    public String getAccountsDirName() {
        return accountsDirName;
    }

    /**
     * Sets the directory name for account files.
     *
     * @param accountsDirName directory name
     */
    public void setAccountsDirName(String accountsDirName) {
        this.accountsDirName = accountsDirName;
    }

    /**
     * Returns the directory name for posting rules files.
     *
     * @return posting rules directory name
     */
    public String getPostingRulesDirName() {
        return postingRulesDirName;
    }

    /**
     * Sets the directory name for posting rules files.
     *
     * @param postingRulesDirName directory name
     */
    public void setPostingRulesDirName(String postingRulesDirName) {
        this.postingRulesDirName = postingRulesDirName;
    }

    /**
     * Returns the directory name for general ledger profile files.
     *
     * @return general ledger profile directory name
     */
    public String getGeneralLedgerProfilesDirName() {
        return generalLedgerProfilesDirName;
    }

    /**
     * Sets the directory name for general ledger profile files.
     *
     * @param generalLedgerProfilesDirName directory name
     */
    public void setGeneralLedgerProfilesDirName(String generalLedgerProfilesDirName) {
        this.generalLedgerProfilesDirName = generalLedgerProfilesDirName;
    }

    /**
     * Returns the directory name for ABOR configuration files.
     *
     * @return ABOR configuration directory name
     */
    public String getAborConfigurationsDirName() {
        return aborConfigurationsDirName;
    }

    /**
     * Sets the directory name for ABOR configuration files.
     *
     * @param aborConfigurationsDirName directory name
     */
    public void setAborConfigurationsDirName(String aborConfigurationsDirName) {
        this.aborConfigurationsDirName = aborConfigurationsDirName;
    }

    /**
     * Returns the directory name for ABOR files.
     *
     * @return ABOR directory name
     */
    public String getAborDirName() {
        return aborDirName;
    }

    /**
     * Sets the directory name for ABOR files.
     *
     * @param aborDirName directory name
     */
    public void setAborDirName(String aborDirName) {
        this.aborDirName = aborDirName;
    }

    /**
     * Resolves the changed files root as a {@link Path}.
     *
     * @return path to changed files
     */
    public Path changedFilesRoot() {
        return Path.of(changedFilesDir);
    }

    /**
     * Resolves the plan directory path, applying environment overrides when present.
     *
     * @return plan directory path
     */
    public Path planDirPath() {
        return Path.of(resolveFromEnv("PLAN_DIR", planDir));
    }

    /**
     * Resolves the full master plan file path, applying environment overrides when present.
     *
     * @return master plan path
     */
    public Path masterPlanPath() {
        String fileName = resolveFromEnv("MASTER_PLAN_FILE", masterPlanFile);
        return planDirPath().resolve(fileName);
    }

    private String resolveFromEnv(String envKey, String fallback) {
        String value = envLookup.lookup(envKey);
        return value != null && !value.isBlank() ? value : fallback;
    }

    /**
     * Test hook to override environment lookup without touching real env vars.
     */
    public void setEnvLookup(EnvironmentLookup envLookup) {
        this.envLookup = Objects.requireNonNull(envLookup);
    }
}
