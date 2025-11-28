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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getPostingModuleCode() {
        return postingModuleCode;
    }

    public void setPostingModuleCode(String postingModuleCode) {
        this.postingModuleCode = postingModuleCode;
    }

    public PostingModuleRequest getPostingModuleRequest() {
        return postingModuleRequest;
    }

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
