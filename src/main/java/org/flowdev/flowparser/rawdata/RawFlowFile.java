package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawFlowFile {
    private RawVersion version;
    private List<RawFlow> flows;

    public RawVersion getVersion() {
        return version;
    }

    public void setVersion(RawVersion version) {
        this.version = version;
    }

    public List<RawFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<RawFlow> flows) {
        this.flows = flows;
    }
}
