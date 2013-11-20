package org.flowdev.flowparser.cook;

import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.*;
import org.flowdev.flowparser.rawdata.*;

import java.util.*;

/**
 * This operation reads the content of a file as a UTF-8 text into a string.
 */
public class CookFlowFile<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
        public Getter<T, String> getFileName;
        public Getter<T, RawFlowFile> getRawFlowFile;
        public Setter<FlowFile, T, T> setCookedFlowFile;
    }

    private static class OpData {
        Operation op;
        Set<String> inPorts = new HashSet<>();
        Set<String> outPorts = new HashSet<>();

        OpData(Operation op) {
            this.op = op;
        }
    }

    private final Params<T> params;

    public CookFlowFile(Params<T> params) {
        this.params = params;
    }

    protected T filter(T data) {
        RawFlowFile rawFlowFile = params.getRawFlowFile.get(data);
        FlowFile cookedFlowFile = new FlowFile();

        cookedFlowFile.fileName = params.getFileName.get(data);
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
        cookConnections(rflow, flow);
        cookOperations(rflow, flow);
        return flow;
    }

    private void cookConnections(RawFlow rflow, Flow flow) {
        flow.connections = new ArrayList<>();
        for (RawConnectionChain connChain : rflow.connections) {
            cookConnectionChain(connChain, flow.connections);
        }
    }

    private void cookConnectionChain(RawConnectionChain chain, List<Connection> connections) {
        if (chain.inPort != null) {
            RawConnectionPart toPart = chain.parts.get(0);
            addConnection(chain.inPort, null, toPart.inPort, toPart.operation, connections);
        }
        int last = chain.parts.size() - 1;
        int i;
        for (i = 0; i < last; i++) {
            RawConnectionPart fromPart = chain.parts.get(i);
            RawConnectionPart toPart = chain.parts.get(i + 1);
            addConnection(fromPart.outPort, fromPart.operation, toPart.inPort, toPart.operation, connections);
        }
        if (chain.outPort != null) {
            RawConnectionPart fromPart = chain.parts.get(last);
            addConnection(fromPart.outPort, fromPart.operation, chain.outPort, null, connections);
        }
    }

    private void addConnection(RawPort fromPort, RawOperation fromOp, RawPort toPort, RawOperation toOp, List<Connection> connections) {
        Connection conn = new Connection();

        conn.fromPort = fromPort.name;
        conn.fromOp = (fromOp == null) ? null : fromOp.name;
        conn.toPort = toPort.name;
        conn.toOp = (toOp == null) ? null : toOp.name;

        if (fromPort.dataType != null) {
            conn.dataType = fromPort.dataType.type;
            conn.showDataType = fromPort.dataType.fromDsl;
        } else if (toPort.dataType != null) {
            conn.dataType = toPort.dataType.type;
            conn.showDataType = toPort.dataType.fromDsl;
        }

        connections.add(conn);
    }

    private void cookOperations(RawFlow rflow, Flow flow) {
        Map<String, OpData> operationMap = new HashMap<>();
        for (RawConnectionChain chain : rflow.connections) {
            for (RawConnectionPart part : chain.parts) {
                cookOperation(part, operationMap);
            }
        }

        flow.operations = new ArrayList<>(operationMap.values().size());
        for (OpData opData : operationMap.values()) {
            flow.operations.add(opData.op);
        }
    }

    private void cookOperation(RawConnectionPart part, Map<String, OpData> operationMap) {
        OpData opData = operationMap.get(part.operation.name);
        Operation op;
        if (opData == null) {
            op = new Operation();
            op.name = part.operation.name;
            op.type = (part.operation.type == null) ? null : part.operation.type.type;
            op.ports = new ArrayList<>();
            opData = new OpData(op);
            operationMap.put(op.name, opData);
        } else {
            op = opData.op;
            if (op.type == null && part.operation.type != null) {
                op.type = part.operation.type.type;
            }
        }

        addPorts(part.inPort, part.outPort, op, opData.inPorts, opData.outPorts);
    }

    private void addPorts(RawPort inPort, RawPort outPort, Operation op, Set<String> inPorts, Set<String> outPorts) {
        if (inPort != null) {
            addMyPort(inPort, op, inPorts, outPorts.size(), true);
        }
        if (outPort != null) {
            addMyPort(outPort, op, outPorts, inPorts.size(), false);
        }
    }

    private void addMyPort(RawPort myPort, Operation op, Set<String> myPorts, int otherPortsCount, boolean isInPort) {
        if (myPorts.contains(myPort.name)) {
            return;
        }

        int reusePair = otherPortsCount - myPorts.size();
        PortPair portPair;
        if (reusePair > 0) {
            portPair = op.ports.get(op.ports.size() - reusePair);
        } else {
            portPair = new PortPair();
            portPair.isLast = true;
            op.ports.add(portPair);

            // old last isn't last anymore:
            if (op.ports.size() > 1) {
                op.ports.get(op.ports.size() - 2).isLast = false;
            }
        }
        myPorts.add(myPort.name);
        if (isInPort) {
            portPair.inPort = myPort.name;
        } else {
            portPair.outPort = myPort.name;
        }
    }

    private Version cookVersion(RawVersion rawVers) {
        Version vers = new Version();
        vers.political = rawVers.political;
        vers.major = rawVers.major;
        return vers;
    }
}
