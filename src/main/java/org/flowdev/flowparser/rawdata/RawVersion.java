package org.flowdev.flowparser.rawdata;


public class RawVersion extends RawNode {
    private long political;
    private long major;

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
}
