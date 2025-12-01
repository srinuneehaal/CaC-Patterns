package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.AborConfigurationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AborConfigurationFile {

    private String scope;

    @JsonProperty("aborConfigurationRequestList")
    private List<AborConfigurationRequest> aborConfigurations = new ArrayList<>();

    /**
     * Returns the scope that the ABOR configuration belongs to.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the ABOR configuration.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the list of ABOR configuration requests.
     *
     * @return configuration requests
     */
    public List<AborConfigurationRequest> getAborConfigurations() {
        return aborConfigurations;
    }

    /**
     * Sets the list of ABOR configuration requests.
     *
     * @param aborConfigurations configuration requests
     */
    public void setAborConfigurations(List<AborConfigurationRequest> aborConfigurations) {
        this.aborConfigurations = aborConfigurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AborConfigurationFile that = (AborConfigurationFile) o;
        return Objects.equals(scope, that.scope)
                && Objects.equals(aborConfigurations, that.aborConfigurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, aborConfigurations);
    }
}
