package org.flowdev.flowparser.rawdata;

public class RawOperation extends RawNode {
    private String name;
    private RawDataType type;

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
}
