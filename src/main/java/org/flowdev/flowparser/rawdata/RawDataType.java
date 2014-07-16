package org.flowdev.flowparser.rawdata;


public class RawDataType extends RawNode {
    private String type;
    private boolean fromDsl;

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
}
