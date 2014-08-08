package org.flowdev.flowparser.rawdata;


public class RawConnectionPart {
    private RawPort inPort;
    private RawOperation operation;
    private RawPort outPort;
    private String sourcePosition;

    public RawPort getInPort() {
        return inPort;
    }

    public void setInPort(RawPort inPort) {
        this.inPort = inPort;
    }

    public RawOperation getOperation() {
        return operation;
    }

    public void setOperation(RawOperation operation) {
        this.operation = operation;
    }

    public RawPort getOutPort() {
        return outPort;
    }

    public void setOutPort(RawPort outPort) {
        this.outPort = outPort;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
