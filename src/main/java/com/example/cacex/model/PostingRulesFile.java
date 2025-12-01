package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.PostingModuleRequest;

import java.util.Objects;

public class PostingRulesFile {

    private String scope;
    private String chartOfAccountsCode;
    private String postingModuleCode;
    @JsonProperty("postingModuleRequest")
    private PostingModuleRequest postingModuleRequest;

    /**
     * Returns the scope for the posting rules file.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the posting rules file.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the chart of accounts code associated with the posting rules.
     *
     * @return chart of accounts code
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chart of accounts code associated with the posting rules.
     *
     * @param chartOfAccountsCode chart of accounts code
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Returns the posting module code.
     *
     * @return posting module code
     */
    public String getPostingModuleCode() {
        return postingModuleCode;
    }

    /**
     * Sets the posting module code.
     *
     * @param postingModuleCode posting module code
     */
    public void setPostingModuleCode(String postingModuleCode) {
        this.postingModuleCode = postingModuleCode;
    }

    /**
     * Returns the posting module request payload.
     *
     * @return posting module request
     */
    public PostingModuleRequest getPostingModuleRequest() {
        return postingModuleRequest;
    }

    /**
     * Sets the posting module request payload.
     *
     * @param postingModuleRequest posting module request
     */
    public void setPostingModuleRequest(PostingModuleRequest postingModuleRequest) {
        this.postingModuleRequest = postingModuleRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostingRulesFile that = (PostingRulesFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(chartOfAccountsCode, that.chartOfAccountsCode)
                && Objects.equals(postingModuleCode, that.postingModuleCode)
                && Objects.equals(postingModuleRequest, that.postingModuleRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, chartOfAccountsCode, postingModuleCode, postingModuleRequest);
    }

    @Override
    public String toString() {
        return "PostingRulesFile{" +
                "scope='" + scope + '\'' +
                ", chartOfAccountsCode='" + chartOfAccountsCode + '\'' +
                ", postingModuleCode='" + postingModuleCode + '\'' +
                ", postingModuleRequest=" + postingModuleRequest +
                '}';
    }
}
