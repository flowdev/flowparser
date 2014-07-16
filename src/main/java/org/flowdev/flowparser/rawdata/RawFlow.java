package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawFlow extends RawNode {
    private String name;
    private List<RawConnectionChain> connections;

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
}
