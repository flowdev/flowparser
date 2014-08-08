package org.flowdev.flowparser.rawdata;

public class RawOperation {
    private String name;
    private RawDataType type;
    private String sourcePosition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RawDataType getType() {
        return type;
    }

    public void setType(RawDataType type) {
        this.type = type;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
