package org.flowdev.flowparser.data;

import java.util.List;


public class Operation {
    private String name;
    private String type;
    private List<PortPair> ports;
    private int srcPos;


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

    public int srcPos() {
        return this.srcPos;
    }

    public Operation srcPos(final int srcPos) {
        this.srcPos = srcPos;
        return this;
    }
}
