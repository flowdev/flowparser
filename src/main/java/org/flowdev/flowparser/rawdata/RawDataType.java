package org.flowdev.flowparser.rawdata;


public class RawDataType {
    private String type;
    private boolean fromDsl;
    private String sourcePosition;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFromDsl() {
        return fromDsl;
    }

    public void setFromDsl(boolean fromDsl) {
        this.fromDsl = fromDsl;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
