package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.AborRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AborFile {

    private String scope;

    @JsonProperty("aborRequestList")
    private List<AborRequest> aborRequests = new ArrayList<>();

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<AborRequest> getAborRequests() {
        return aborRequests;
    }

    public void setAborRequests(List<AborRequest> aborRequests) {
        this.aborRequests = aborRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AborFile aborFile = (AborFile) o;
        return Objects.equals(scope, aborFile.scope) && Objects.equals(aborRequests, aborFile.aborRequests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, aborRequests);
    }
}
