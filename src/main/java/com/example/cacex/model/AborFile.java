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

    /**
     * Returns the scope for the ABOR entries.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the ABOR entries.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the list of ABOR requests.
     *
     * @return ABOR requests
     */
    public List<AborRequest> getAborRequests() {
        return aborRequests;
    }

    /**
     * Sets the list of ABOR requests.
     *
     * @param aborRequests ABOR requests
     */
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
