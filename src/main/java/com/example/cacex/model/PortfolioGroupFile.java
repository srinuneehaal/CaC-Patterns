package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PortfolioGroupFile {

    private String scope;

    @JsonProperty("createPortfolioGroupRequestList")
    private List<CreatePortfolioGroupRequest> groups = new ArrayList<>();

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<CreatePortfolioGroupRequest> getGroups() {
        return groups;
    }

    public void setGroups(List<CreatePortfolioGroupRequest> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortfolioGroupFile that = (PortfolioGroupFile) o;
        return Objects.equals(scope, that.scope) && Objects.equals(groups, that.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, groups);
    }
}
