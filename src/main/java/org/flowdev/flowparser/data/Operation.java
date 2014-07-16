package org.flowdev.flowparser.data;

import java.util.List;


public class Operation {
    private String name;
    private String type;
    private List<PortPair> ports;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PortPair> getPorts() {
        return ports;
    }

    public void setPorts(List<PortPair> ports) {
        this.ports = ports;
    }
}
