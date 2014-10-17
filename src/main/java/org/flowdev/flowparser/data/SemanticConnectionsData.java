package org.flowdev.flowparser.data;

import org.flowdev.flowparser.semantic.AddOpResult;

import java.util.List;
import java.util.Map;

public class SemanticConnectionsData {
    private MainData mainData;
    private Map<String, Operation> ops;
    private List<Connection> conns;
    private AddOpResult addOpResult;
    private Operation newOp;

    public MainData mainData() {
        return this.mainData;
    }

    public Map<String, Operation> ops() {
        return this.ops;
    }

    public List<Connection> conns() {
        return this.conns;
    }

    public AddOpResult addOpResult() {
        return this.addOpResult;
    }

    public SemanticConnectionsData mainData(final MainData mainData) {
        this.mainData = mainData;
        return this;
    }

    public SemanticConnectionsData ops(final Map<String, Operation> ops) {
        this.ops = ops;
        return this;
    }

    public SemanticConnectionsData conns(final List<Connection> conns) {
        this.conns = conns;
        return this;
    }

    public SemanticConnectionsData addOpResult(final AddOpResult addOpResult) {
        this.addOpResult = addOpResult;
        return this;
    }

    public Operation newOp() {
        return this.newOp;
    }

    public SemanticConnectionsData newOp(final Operation newOp) {
        this.newOp = newOp;
        return this;
    }

}
