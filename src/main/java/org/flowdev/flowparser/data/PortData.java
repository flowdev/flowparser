package org.flowdev.flowparser.data;

public class PortData {
    private String name;
    private boolean hasIndex;
    private int index;

    public String name() {
        return this.name;
    }

    public PortData name(String name) {
        this.name = name;
        return this;
    }

    public boolean hasIndex() {
        return this.hasIndex;
    }

    public PortData hasIndex(boolean hasIndex) {
        this.hasIndex = hasIndex;
        return this;
    }

    public int index() {
        return this.index;
    }

    public PortData index(int index) {
        this.index = index;
        return this;
    }
}
