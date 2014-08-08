package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawFlow {
    private String name;
    private List<RawConnectionChain> connections;
    private String sourcePosition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RawConnectionChain> getConnections() {
        return connections;
    }

    public void setConnections(List<RawConnectionChain> connections) {
        this.connections = connections;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
