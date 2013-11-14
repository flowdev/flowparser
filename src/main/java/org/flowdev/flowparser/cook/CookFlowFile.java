package org.flowdev.flowparser.cook;

import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.*;
import org.flowdev.flowparser.rawdata.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This operation reads the content of a file as a UTF-8 text into a string.
 */
public class CookFlowFile<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
        public Getter<T, String> getFileName;
        public Getter<T, RawFlowFile> getRawFlowFile;
        public Setter<FlowFile, T, T> setCookedFlowFile;
    }

    private final Params<T> params;
    private final String sourceRoot = System.getProperty("source.base", ".");
    private String fileName;

    public CookFlowFile(Params<T> params) {
        this.params = params;
    }

    protected T filter(T data) {
        fileName = params.getFileName.get(data);
        RawFlowFile rawFlowFile = params.getRawFlowFile.get(data);
        FlowFile cookedFlowFile = new FlowFile();

        cookedFlowFile.version = cookVersion(rawFlowFile.version);
        cookedFlowFile.flows = cookFlows(rawFlowFile.flows);

        params.setCookedFlowFile.set(data, cookedFlowFile);
        return data;
    }

    private List<Flow> cookFlows(List<RawFlow> rawFlows) {
        List<Flow> flows = new ArrayList<>(rawFlows.size());
        for (RawFlow rflow : rawFlows) {
            flows.add(cookFlow(rflow));
        }
        return flows;
    }

    private Flow cookFlow(RawFlow rflow) {
        Flow flow = new Flow();
        flow.name = rflow.name;
        checkConnections(rflow);
        cookConnections(rflow, flow);
        return flow;
    }

    private void checkConnections(RawFlow rflow) {
        for (RawConnectionChain connectionChain : rflow.connections) {
            if (connectionChain.parts.size() < 2) {
                throw new RuntimeException(
                        "ERROR: Found connection chain with less than two parts."
                                + connectionChain.sourcePosition + ": "
                                + PrettyPrinter.prettyPrint(connectionChain));
            }
        }
    }

    private RawConnectionPart findInConnectionPart(String portName,
                                                   RawFlow rflow) {
        for (RawConnectionChain connChain : rflow.connections) {
            RawConnectionPart connPart = connChain.parts.get(0);
            if (isParentPart(connPart) && portName.equals(connPart.inPort.name)) {
                return connChain.parts.get(1);
            }
        }
        throw new RuntimeException("Unable to find input port '" + portName
                + "' in the connections of the raw flow: "
                + PrettyPrinter.prettyPrint(rflow));
    }

    private RawConnectionPart findOutConnectionPart(String portName,
                                                    RawFlow rflow) {
        for (RawConnectionChain connChain : rflow.connections) {
            int last = connChain.parts.size() - 1;
            RawConnectionPart connPart = connChain.parts.get(last);
            if (isParentPart(connPart)
                    && portName.equals(connPart.outPort.name)) {
                return connChain.parts.get(last - 1);
            }
        }
        throw new RuntimeException("Unable to find output port '" + portName
                + "in raw flow: " + PrettyPrinter.prettyPrint(rflow));
    }

    private void cookConnections(RawFlow rflow, Flow flow) {
        flow.connections = new ArrayList<>();
        for (RawConnectionChain connChain : rflow.connections) {
            int last = connChain.parts.size() - 1;
            for (int i = 0; i < last; i++) {
                addConnection(connChain.parts.get(i),
                        connChain.parts.get(i + 1), flow.connections);
            }
        }
    }

    private void addConnection(RawConnectionPart from, RawConnectionPart to,
                               List<Connection> conns) {
        if (!isParentPart(from) && !isParentPart(to)) {
            Connection conn = new Connection();
            conn.fromOp = from.operation.name;
            conn.fromPort = from.outPort.name;
            conn.toOp = to.operation.name;
            conn.toPort = to.inPort.name;

            conns.add(conn);
        }
    }

    private void cookOperations(RawFlow rflow, Flow flow) {
        flow.operations = new ArrayList<>();
        for (RawOperation rawOp : rflow.operations) {
            flow.operations.add(cookOperation(rawOp));
        }
    }

    private Operation cookOperation(RawOperation rawOp) {
        Operation op = new Operation();
        op.name = rawOp.name;
        op.type = rawOp.type.type;

        return op;
    }

    private Version cookVersion(RawVersion rawVers) {
        Version vers = new Version();
        vers.political = rawVers.political;
        vers.major = rawVers.major;
        return vers;
    }

    private boolean isParentPart(RawConnectionPart part) {
        return part.operation == null;
    }
}
