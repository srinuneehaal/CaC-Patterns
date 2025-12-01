package com.example.cacex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finbourne.lusid.model.SideDefinitionRequest;

import java.util.Objects;

public class SideFile {

    private String scope;
    private String side;
    @JsonProperty("sideDefinitionRequest")
    private SideDefinitionRequest sideDefinition;

    /**
     * Returns the scope for the side definition.
     *
     * @return scope value
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope for the side definition.
     *
     * @param scope scope value
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the side name (e.g., BUY or SELL).
     *
     * @return side name
     */
    public String getSide() {
        return side;
    }

    /**
     * Sets the side name (e.g., BUY or SELL).
     *
     * @param side side name
     */
    public void setSide(String side) {
        this.side = side;
    }

    /**
     * Returns the side definition payload.
     *
     * @return side definition
     */
    public SideDefinitionRequest getSideDefinition() {
        return sideDefinition;
    }

    /**
     * Sets the side definition payload.
     *
     * @param sideDefinition side definition
     */
    public void setSideDefinition(SideDefinitionRequest sideDefinition) {
        this.sideDefinition = sideDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SideFile sideFile = (SideFile) o;
        return Objects.equals(scope, sideFile.scope)
                && Objects.equals(side, sideFile.side)
                && Objects.equals(sideDefinition, sideFile.sideDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, side, sideDefinition);
    }

    @Override
    public String toString() {
        return "SideFile{" +
                "scope='" + scope + '\'' +
                ", side='" + side + '\'' +
                ", sideDefinition=" + sideDefinition +
                '}';
    }
}
