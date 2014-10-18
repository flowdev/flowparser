package org.flowdev.flowparser.semantic.connections;

import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;

public class AddOpResult {
    private Operation op;
    private PortData outPort;
    private boolean outPortAdded;

    public Operation op() {
        return this.op;
    }

    public PortData outPort() {
        return this.outPort;
    }

    public boolean outPortAdded() {
        return this.outPortAdded;
    }

    public AddOpResult op(final Operation op) {
        this.op = op;
        return this;
    }

    public AddOpResult outPort(final PortData outPort) {
        this.outPort = outPort;
        return this;
    }

    public AddOpResult outPortAdded(final boolean outPortAdded) {
        this.outPortAdded = outPortAdded;
        return this;
    }
}
