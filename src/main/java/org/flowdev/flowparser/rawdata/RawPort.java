package org.flowdev.flowparser.rawdata;


public class RawPort {
    private String name;
    private Integer index;
    private RawPortType type;
    private RawDataType dataType;
    private String sourcePosition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public RawPortType getType() {
        return type;
    }

    public void setType(RawPortType type) {
        this.type = type;
    }

    public RawDataType getDataType() {
        return dataType;
    }

    public void setDataType(RawDataType dataType) {
        this.dataType = dataType;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
