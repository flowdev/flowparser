package org.flowdev.flowparser.data;

import java.util.List;


public class Operation {
    private String name;
    private String type;
    private List<PortPair> ports;


    public Operation name(final String name) {
        this.name = name;
        return this;
    }

    public Operation type(final String type) {
        this.type = type;
        return this;
    }

    public Operation ports(final List<PortPair> ports) {
        this.ports = ports;
        return this;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public List<PortPair> ports() {
        return ports;
    }
}
