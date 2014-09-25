package org.flowdev.flowparser.data;


public class Connection {
    private Operation fromOp;
    private PortData fromPort;
    private String capFromPort;
    private String dataType;
    private boolean showDataType;
    private Operation toOp;
    private PortData toPort;
    private String capToPort;

    public Connection fromOp(final Operation fromOp) {
        this.fromOp = fromOp;
        return this;
    }

    public Connection fromPort(final PortData fromPort) {
        this.fromPort = fromPort;
        return this;
    }

    public Connection capFromPort(final String capFromPort) {
        this.capFromPort = capFromPort;
        return this;
    }

    public Connection dataType(final String dataType) {
        this.dataType = dataType;
        return this;
    }

    public Connection showDataType(final boolean showDataType) {
        this.showDataType = showDataType;
        return this;
    }

    public Connection toOp(final Operation toOp) {
        this.toOp = toOp;
        return this;
    }

    public Connection toPort(final PortData toPort) {
        this.toPort = toPort;
        return this;
    }

    public Connection capToPort(final String capToPort) {
        this.capToPort = capToPort;
        return this;
    }

    public Operation fromOp() {
        return fromOp;
    }

    public PortData fromPort() {
        return fromPort;
    }

    public String capFromPort() {
        return capFromPort;
    }

    public String dataType() {
        return dataType;
    }

    public boolean showDataType() {
        return showDataType;
    }

    public Operation toOp() {
        return toOp;
    }

    public PortData toPort() {
        return toPort;
    }

    public String capToPort() {
        return capToPort;
    }
}
