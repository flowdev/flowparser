package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawConnectionChain {
    private RawPort inPort;
    private List<RawConnectionPart> parts;
    private RawPort outPort;
    private String sourcePosition;

    public RawPort getInPort() {
        return inPort;
    }

    public void setInPort(RawPort inPort) {
        this.inPort = inPort;
    }

    public List<RawConnectionPart> getParts() {
        return parts;
    }

    public void setParts(List<RawConnectionPart> parts) {
        this.parts = parts;
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
