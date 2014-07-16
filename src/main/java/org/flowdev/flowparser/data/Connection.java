package org.flowdev.flowparser.data;


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

    public String getFromOp() {
        return fromOp;
    }

    public void setFromOp(String fromOp) {
        this.fromOp = fromOp;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public String getCapFromPort() {
        return capFromPort;
    }

    public void setCapFromPort(String capFromPort) {
        this.capFromPort = capFromPort;
    }

    public boolean isHasFromPortIndex() {
        return hasFromPortIndex;
    }

    public void setHasFromPortIndex(boolean hasFromPortIndex) {
        this.hasFromPortIndex = hasFromPortIndex;
    }

    public int getFromPortIndex() {
        return fromPortIndex;
    }

    public void setFromPortIndex(int fromPortIndex) {
        this.fromPortIndex = fromPortIndex;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isShowDataType() {
        return showDataType;
    }

    public void setShowDataType(boolean showDataType) {
        this.showDataType = showDataType;
    }

    public String getToOp() {
        return toOp;
    }

    public void setToOp(String toOp) {
        this.toOp = toOp;
    }

    public String getToPort() {
        return toPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    public String getCapToPort() {
        return capToPort;
    }

    public void setCapToPort(String capToPort) {
        this.capToPort = capToPort;
    }

    public boolean isHasToPortIndex() {
        return hasToPortIndex;
    }

    public void setHasToPortIndex(boolean hasToPortIndex) {
        this.hasToPortIndex = hasToPortIndex;
    }

    public int getToPortIndex() {
        return toPortIndex;
    }

    public void setToPortIndex(int toPortIndex) {
        this.toPortIndex = toPortIndex;
    }
}
