package org.flowdev.flowparser.rawdata;


public class RawConnectionPart extends RawNode {
    private RawPort inPort;
    private RawOperation operation;
    private RawPort outPort;

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
}
