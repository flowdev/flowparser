package org.flowdev.flowparser.data;


public class PortPair {
    private String inPort;
    private boolean hasInPortIndex;
    private int inPortIndex;
    private String outPort;
    private boolean hasOutPortIndex;
    private int outPortIndex;
    private boolean isLast;

    public PortPair inPort(final String inPort) {
        this.inPort = inPort;
        return this;
    }

    public PortPair hasInPortIndex(boolean hasIndex) {
        this.hasInPortIndex = hasIndex;
        return this;
    }

    public PortPair inPortIndex(int index) {
        this.inPortIndex = index;
        return this;
    }

    public PortPair outPort(String outPort) {
        this.outPort = outPort;
        return this;
    }

    public PortPair hasOutPortIndex(boolean hasIndex) {
        this.hasOutPortIndex = hasIndex;
        return this;
    }

    public PortPair outPortIndex(int index) {
        this.outPortIndex = index;
        return this;
    }

    public PortPair isLast(final boolean isLast) {
        this.isLast = isLast;
        return this;
    }

    public String inPort() {
        return inPort;
    }

    public int inPortIndex() {
        return inPortIndex;
    }

    public boolean hasInPortIndex() {
        return hasInPortIndex;
    }

    public String outPort() {
        return outPort;
    }

    public boolean hasOutPortIndex() {
        return hasOutPortIndex;
    }

    public int outPortIndex() {
        return outPortIndex;
    }

    public boolean isLast() {
        return isLast;
    }
}
