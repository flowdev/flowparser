package org.flowdev.flowparser.cook;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.MainData;
import org.flowdev.flowparser.data.*;
import org.flowdev.flowparser.rawdata.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This operation reads the content of a file as a UTF-8 text into a string.
 */
public class CookFlowFile extends FilterOp<MainData, NoConfig> {
    private static class OpData {
        @SuppressWarnings("CanBeFinal")
        Operation op;
        @SuppressWarnings("CanBeFinal")
        Set<String> inPorts = new HashSet<>();
        @SuppressWarnings("CanBeFinal")
        Set<String> outPorts = new HashSet<>();

        OpData(Operation op) {
            this.op = op;
        }
    }

    protected void filter(MainData data) {
        FlowFile cookedFlowFile = new FlowFile();

        cookedFlowFile.setFileName(data.fileName);
        cookedFlowFile.setVersion(cookVersion(data.rawFlowFile.getVersion()));
        cookedFlowFile.setFlows(cookFlows(data.rawFlowFile.getFlows()));

        data.flowFile = cookedFlowFile;
        outPort.send(data);
    }

    private List<Flow> cookFlows(List<RawFlow> rawFlows) {
        return rawFlows.stream().map(this::cookFlow).collect(Collectors.toList());
    }

    private Flow cookFlow(RawFlow rflow) {
        Flow flow = new Flow();
        flow.setName(rflow.getName());
        cookConnections(rflow, flow);
        cookOperations(rflow, flow);
        return flow;
    }

    private void cookConnections(RawFlow rflow, Flow flow) {
        flow.setConnections(new ArrayList<>());
        for (RawConnectionChain connChain : rflow.getConnections()) {
            cookConnectionChain(connChain, flow.getConnections());
        }
    }

    private void cookConnectionChain(RawConnectionChain chain, List<Connection> connections) {
        if (chain.getInPort() != null) {
            RawConnectionPart toPart = chain.getParts().get(0);
            addConnection(chain.getInPort(), null, toPart.getInPort(), toPart.getOperation(), connections);
        }
        int last = chain.getParts().size() - 1;
        int i;
        for (i = 0; i < last; i++) {
            RawConnectionPart fromPart = chain.getParts().get(i);
            RawConnectionPart toPart = chain.getParts().get(i + 1);
            addConnection(fromPart.getOutPort(), fromPart.getOperation(), toPart.getInPort(), toPart.getOperation(), connections);
        }
        if (chain.getOutPort() != null) {
            RawConnectionPart fromPart = chain.getParts().get(last);
            addConnection(fromPart.getOutPort(), fromPart.getOperation(), chain.getOutPort(), null, connections);
        }
    }

    private void addConnection(RawPort fromPort, RawOperation fromOp, RawPort toPort, RawOperation toOp, List<Connection> connections) {
        Connection conn = new Connection();

        conn.setFromPort(fromPort.getName());
        conn.setFromOp((fromOp == null) ? null : fromOp.getName());
        conn.setToPort(toPort.getName());
        conn.setToOp((toOp == null) ? null : toOp.getName());

        if (fromPort.getIndex() != null) {
            conn.setHasFromPortIndex(true);
            conn.setFromPortIndex(fromPort.getIndex());
        }
        if (toPort.getIndex() != null) {
            conn.setHasToPortIndex(true);
            conn.setToPortIndex(toPort.getIndex());
        }

        if (fromPort.getDataType() != null) {
            conn.setDataType(fromPort.getDataType().getType());
            conn.setShowDataType(fromPort.getDataType().isFromDsl());
        } else if (toPort.getDataType() != null) {
            conn.setDataType(toPort.getDataType().getType());
            conn.setShowDataType(toPort.getDataType().isFromDsl());
        }

        conn.setCapFromPort(capitalize(conn.getFromPort()));
        conn.setCapToPort(capitalize(conn.getToPort()));
        connections.add(conn);
    }

    private void cookOperations(RawFlow rflow, Flow flow) {
        Map<String, OpData> operationMap = new HashMap<>();
        for (RawConnectionChain chain : rflow.getConnections()) {
            for (RawConnectionPart part : chain.getParts()) {
                cookOperation(part, operationMap);
            }
        }

        flow.setOperations(new ArrayList<>(operationMap.values().size()));
        for (OpData opData : operationMap.values()) {
            flow.getOperations().add(opData.op);
        }
    }

    private void cookOperation(RawConnectionPart part, Map<String, OpData> operationMap) {
        OpData opData = operationMap.get(part.getOperation().getName());
        Operation op;
        if (opData == null) {
            op = new Operation();
            op.setName(part.getOperation().getName());
            op.setType((part.getOperation().getType() == null) ? null : part.getOperation().getType().getType());
            op.setPorts(new ArrayList<>());
            opData = new OpData(op);
            operationMap.put(op.getName(), opData);
        } else {
            op = opData.op;
            if (op.getType() == null && part.getOperation().getType() != null) {
                op.setType(part.getOperation().getType().getType());
            }
        }

        addPorts(part.getInPort(), part.getOutPort(), op, opData.inPorts, opData.outPorts);
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
        String portName = createPortName(myPort);
        if (myPorts.contains(portName)) {
            return;
        }

        int reusePair = otherPortsCount - myPorts.size();
        PortPair portPair;
        if (reusePair > 0) {
            portPair = op.getPorts().get(op.getPorts().size() - reusePair);
        } else {
            portPair = new PortPair();
            portPair.setLast(true);
            op.getPorts().add(portPair);

            // old last isn't last anymore:
            if (op.getPorts().size() > 1) {
                op.getPorts().get(op.getPorts().size() - 2).setLast(false);
            }
        }
        myPorts.add(portName);
        if (isInPort) {
            portPair.setInPort(portName);
        } else {
            portPair.setOutPort(portName);
        }
    }

    private static String createPortName(RawPort port) {
        if (port.getIndex() == null) {
            return port.getName();
        } else {
            return port.getName() + "." + port.getIndex();
        }
    }

    private Version cookVersion(RawVersion rawVers) {
        return new Version().political((int) rawVers.getPolitical()).major((int) rawVers.getMajor());
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
