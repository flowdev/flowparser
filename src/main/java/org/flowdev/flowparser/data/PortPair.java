package org.flowdev.flowparser.data;


public class PortPair {
    private String inPort;
    private String outPort;
    private boolean isLast;

    public PortPair inPort(final String inPort) {
        this.inPort = inPort;
        return this;
    }

    public PortPair outPort(final String outPort) {
        this.outPort = outPort;
        return this;
    }

    public PortPair isLast(final boolean isLast) {
        this.isLast = isLast;
        return this;
    }

    public String inPort() {
        return inPort;
    }

    public String outPort() {
        return outPort;
    }

    public boolean isLast() {
        return isLast;
    }
}
