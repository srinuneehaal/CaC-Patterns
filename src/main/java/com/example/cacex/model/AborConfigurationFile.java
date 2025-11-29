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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<AborConfigurationRequest> getAborConfigurations() {
        return aborConfigurations;
    }

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
