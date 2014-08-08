package org.flowdev.flowparser.rawdata;


public class RawVersion {
    private long political;
    private long major;
    private String sourcePosition;

    public long getPolitical() {
        return political;
    }

    public void setPolitical(long political) {
        this.political = political;
    }

    public long getMajor() {
        return major;
    }

    public void setMajor(long major) {
        this.major = major;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
