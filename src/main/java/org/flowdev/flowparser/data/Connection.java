package org.flowdev.flowparser.data;


@SuppressWarnings("UnusedDeclaration")
public class Connection {
    private String fromOp;
    private String fromPort;
    private String capFromPort;
    private boolean hasFromPortIndex;
    private int fromPortIndex;
    private String dataType;
    private boolean showDataType;
    private String toOp;
    private String toPort;
    private String capToPort;
    private boolean hasToPortIndex;
    private int toPortIndex;

    public Connection fromOp(final String fromOp) {
        this.fromOp = fromOp;
        return this;
    }

    public Connection fromPort(final String fromPort) {
        this.fromPort = fromPort;
        return this;
    }

    public Connection capFromPort(final String capFromPort) {
        this.capFromPort = capFromPort;
        return this;
    }

    public Connection hasFromPortIndex(final boolean hasFromPortIndex) {
        this.hasFromPortIndex = hasFromPortIndex;
        return this;
    }

    public Connection fromPortIndex(final int fromPortIndex) {
        this.fromPortIndex = fromPortIndex;
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

    public Connection toOp(final String toOp) {
        this.toOp = toOp;
        return this;
    }

    public Connection toPort(final String toPort) {
        this.toPort = toPort;
        return this;
    }

    public Connection capToPort(final String capToPort) {
        this.capToPort = capToPort;
        return this;
    }

    public Connection hasToPortIndex(final boolean hasToPortIndex) {
        this.hasToPortIndex = hasToPortIndex;
        return this;
    }

    public Connection toPortIndex(final int toPortIndex) {
        this.toPortIndex = toPortIndex;
        return this;
    }

    public String fromOp() {
        return fromOp;
    }

    public String fromPort() {
        return fromPort;
    }

    public String capFromPort() {
        return capFromPort;
    }

    public boolean hasFromPortIndex() {
        return hasFromPortIndex;
    }

    public int fromPortIndex() {
        return fromPortIndex;
    }

    public String dataType() {
        return dataType;
    }

    public boolean showDataType() {
        return showDataType;
    }

    public String toOp() {
        return toOp;
    }

    public String toPort() {
        return toPort;
    }

    public String capToPort() {
        return capToPort;
    }

    public boolean hasToPortIndex() {
        return hasToPortIndex;
    }

    public int toPortIndex() {
        return toPortIndex;
    }
}
