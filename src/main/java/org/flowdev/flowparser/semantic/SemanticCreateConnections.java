package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.*;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.flowdev.parser.util.ParserUtil;

import java.util.*;

import static java.lang.Integer.max;
import static org.flowdev.flowparser.util.PortUtil.*;
import static org.flowdev.parser.util.ParserUtil.addSemanticError;
import static org.flowdev.parser.util.ParserUtil.isOk;

public class SemanticCreateConnections<T> extends FilterOp<T, NoConfig> {
    private static final String TYPE_OUTPUT = "output";

    private final ParserParams<T> params;

    public SemanticCreateConnections(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        Flow flow = createFlow(parserData);
        if (isOk(parserData.result())) {
            parserData.result().value(flow);
        }

        outPort.send(params.setParserData.set(data, parserData));
    }

    /**
     * text input:
     * ( optInPort  [OptDataType]-> optInPort )? opName(OpType) optOutPort
     * ( [OptDataType]-> optInPort opName(OpType) optOutPort )*
     * ( [OptDataType]-> optOutPort )?
     * <p>
     * semantic input:
     * List(multiple1)[
     * List(all)[
     * List(chainBeg)[Connection, Operation], List(multiple0)[ List(chainMid)[arrow, Operation] ], Connection ]
     * ]
     * ]
     *
     * @param parserData the full parser data including the subResults.
     * @return a Flow with connections and operations nicely filled.
     */
    @SuppressWarnings("unchecked")
    private Flow createFlow(ParserData parserData) {
        LinkedHashMap<String, Operation> ops = new LinkedHashMap<>(256);
        List<Connection> conns = new ArrayList<>(256);

        for (ParseResult subResult : parserData.subResults()) {
            Operation lastOp;
            AddOpResult addOpResult = new AddOpResult();
            List<Object> chain = (List<Object>) subResult.value();
            List<Object> chainBeg = (List<Object>) chain.get(0);
            List<Object> chainMids = (List<Object>) chain.get(1);
            Connection chainEnd = (Connection) chain.get(2);

            // handle chainBeg
            Connection connBeg = (Connection) chainBeg.get(0);
            addLastOp(ops, (Operation) chainBeg.get(1), parserData, addOpResult);
            lastOp = addOpResult.op;
            if (connBeg != null) {
                connBeg.toOp(lastOp);
                correctToPort(connBeg, lastOp);
                conns.add(connBeg);
            }

            // handle chainMids
            if (chainMids != null) {
                for (Object chainMidObj : chainMids) {
                    List<Object> chainMid = (List<Object>) chainMidObj;
                    String arrowType = (String) chainMid.get(0);
                    PortData fromPort = addOpResult.outPortPair.outPort();
                    Operation toOp = (Operation) chainMid.get(1);
                    PortData toPort = toOp.portPairs().get(0).inPort();
                    addLastOp(ops, toOp, parserData, addOpResult);
                    toOp = addOpResult.op;

                    Connection connMid = new Connection().dataType(arrowType).showDataType(arrowType != null)
                            .fromOp(lastOp).fromPort(fromPort).toOp(toOp).toPort(toPort);
                    correctToPort(connMid, toOp);
                    conns.add(connMid);

                    lastOp = toOp;
                }
            }

            // handle chainEnd
            if (chainEnd != null) {
                chainEnd.fromOp(lastOp);
                if (addOpResult.outPortPair != null) {
                    chainEnd.fromPort(addOpResult.outPortPair.outPort());
                }
                if (chainEnd.fromPort().name() == null && chainEnd.toPort().name() == null) {
                    chainEnd.fromPort(defaultOutPort(chainEnd.fromPort().srcPos()));
                    chainEnd.toPort(copyPort(chainEnd.fromPort(), chainEnd.toPort().srcPos()));
                } else if (chainEnd.toPort().name() == null) {
                    chainEnd.toPort(copyPort(chainEnd.fromPort(), chainEnd.toPort().srcPos()));
                } else if (chainEnd.fromPort() == null) {
                    chainEnd.fromPort(copyPort(chainEnd.toPort(), chainEnd.fromPort().srcPos()));
                    addOutPort(lastOp, chainEnd.fromPort(), parserData, addOpResult);
                }
                correctFromPort(chainEnd, lastOp);
                conns.add(chainEnd);
            } else if (addOpResult.outPortAdded) {
                addOpResult.outPortPair.outPort(null);
            }
        }
        parserData.result().value(null);

        Flow result = new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
        verifyFlow(result, parserData);
        return new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
    }

    private void correctFromPort(Connection conn, Operation op) {
        for (PortPair portPair : op.portPairs()) {
            Boolean eq = equalPorts(portPair.outPort(), conn.fromPort());
            if (eq == null || eq) {
                conn.fromPort(portPair.outPort());
            }
        }
    }

    private void correctToPort(Connection conn, Operation op) {
        for (PortPair portPair : op.portPairs()) {
            Boolean eq = equalPorts(portPair.inPort(), conn.toPort());
            if (eq == null || eq) {
                conn.toPort(portPair.inPort());
            }
        }
    }

    private void verifyFlow(Flow result, ParserData parserData) {
        verifyOutPortsUsedOnlyOnce(result, parserData);
    }

    private void verifyOutPortsUsedOnlyOnce(Flow result, ParserData parserData) {
        // check for output ports that are connected to multiple input ports:
        Map<String, Set<String>> connMap = new HashMap<>(256);
        for (Connection conn : result.connections()) {
            String fromPort = stringifyPort(conn.fromOp(), conn.fromPort());
            String toPort = stringifyPort(conn.toOp(), conn.toPort());
            Set<String> toPorts = connMap.get(fromPort);
            if (toPorts == null) {
                toPorts = new HashSet<>();
                connMap.put(fromPort, toPorts);
            }
            toPorts.add(toPort);
        }

        for (Map.Entry<String, Set<String>> entry : connMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                ParserUtil.addError(parserData, "The output port '" + entry.getKey() +
                        "' is connected to multiple input ports " + entry.getValue().toString() + "!");
            }
        }
    }

    private String stringifyPort(Operation op, PortData port) {
        StringBuilder sb = new StringBuilder(128);
        if (op == null) {
            sb.append("<FLOW>");
        } else {
            sb.append(op.name());
        }
        sb.append(':').append(port.name());
        if (port.hasIndex()) {
            sb.append('.').append(port.index());
        }
        return sb.toString();
    }

    private void addLastOp(Map<String, Operation> ops, Operation op, ParserData parserData, AddOpResult result) {
        Operation existingOp = ops.get(op.name());
        if (existingOp != null) {
            if (existingOp.type() == null) {
                existingOp.type(op.type());
            } else if (op.type() != null && !op.type().equals(existingOp.type())) {
                addSemanticError(parserData, op.srcPos(), "The operation '" + op.name() +
                        "' has got two different types '" + existingOp.type() + "' and '" + op.type() + "'!");
            }
            addPortPair(existingOp, op.portPairs().get(0), parserData, result);
            result.op = existingOp;
        } else {
            PortPair portPair = op.portPairs().get(0);
            portPair.isLast(true);
            ops.put(op.name(), op);
            result.outPortPair = portPair;
            result.outPortAdded = true;
            result.op = op;
        }
    }

    /**
     * Add a pair of ports to an operation.
     * Ports are only added if they don't exist already.
     *
     * @param op         the operation the ports should be added to.
     * @param portPair   the pair of ports to add.
     * @param parserData needed for error handling.
     * @param result     signals if ports were added.
     */
    private AddOpResult addPortPair(Operation op, PortPair portPair, ParserData parserData, AddOpResult result) {
        addInPort(op, portPair.inPort(), parserData);
        addOutPort(op, portPair.outPort(), parserData, result);
        correctIsLast(op);
        return result;
    }

    private void addInPort(Operation op, PortData newPort, ParserData parserData) {
        if (newPort == null || newPort.name() == null) {
            return;
        }

        for (PortPair oldPort : op.portPairs()) {
            if (oldPort.inPort() == null) {
                oldPort.inPort(newPort);
                return;
            }
            Boolean eq = equalPorts(oldPort.inPort(), newPort);
            if (eq == null) {
                addSemanticError(parserData, max(newPort.srcPos(), oldPort.inPort().srcPos()),
                        "The input port '" + newPort.name() + "' of the operation '" + op.name()
                                + "' is used as indexed and unindexed port in the same flow!");
                return;
            }
            if (eq) {
                return;
            }
        }

        PortPair newPair = new PortPair().inPort(newPort);
        op.portPairs().add(newPair);
    }

    private void addOutPort(Operation op, PortData newPort, ParserData parserData, AddOpResult result) {
        if (newPort == null) {
            return;
        }

        for (PortPair oldPort : op.portPairs()) {
            if (oldPort.outPort() == null) {
                oldPort.outPort(newPort);
                result.outPortPair = oldPort;
                result.outPortAdded = true;
                return;
            }
            Boolean eq = equalPorts(oldPort.outPort(), newPort);
            if (eq == null) {
                addSemanticError(parserData, max(newPort.srcPos(), oldPort.outPort().srcPos()),
                        "The output port '" + newPort.name() + "' of the operation '" + op.name()
                                + "' is used as indexed and unindexed port in the same flow!");
                return;
            }
            if (eq) {
                result.outPortPair = oldPort;
                return;
            }
        }

        PortPair newPair = new PortPair().outPort(newPort);
        op.portPairs().add(newPair);
        result.outPortPair = newPair;
        result.outPortAdded = true;
    }

    private void addPort(Operation op, PortData newPort, GetPortFromPair getPortFromPair, SetPortInPair setPortInPair, String portType, ParserData parserData, AddOpResult result) {
        if (newPort == null) {
            return;
        }

        for (PortPair oldPort : op.portPairs()) {
            PortData port = getPortFromPair.getPort(oldPort);
            if (port == null) {
                setPortInPair.setPort(oldPort, newPort);
                if (TYPE_OUTPUT.equals(portType)) {
                    result.outPortPair = oldPort;
                    result.outPortAdded = true;
                }
                return;
            }
            Boolean eq = equalPorts(port, newPort);
            if (eq == null) {
                addSemanticError(parserData, max(newPort.srcPos(), port.srcPos()),
                        "The " + portType + " port '" + newPort.name() + "' of the operation '" + op.name()
                                + "' is used as indexed and unindexed port in the same flow!");
                return;
            }
            if (eq) {
                if (TYPE_OUTPUT.equals(portType)) {
                    result.outPortPair = oldPort;
                }
                return;
            }
        }

        PortPair newPair = new PortPair();
        setPortInPair.setPort(newPair, newPort);
        op.portPairs().add(newPair);
        if (TYPE_OUTPUT.equals(portType)) {
            result.outPortPair = newPair;
            result.outPortAdded = true;
        }
    }

    private void correctIsLast(Operation op) {
        int n = op.portPairs().size();
        if (n > 1) {
            op.portPairs().get(n - 2).isLast(false);
        }
        op.portPairs().get(n - 1).isLast(true);
    }


    private interface GetPortFromPair {
        PortData getPort(PortPair portPair);
    }

    private interface SetPortInPair {
        void setPort(PortPair portPair, PortData port);
    }

    private static class AddOpResult {
        private Operation op;
        private PortPair outPortPair;
        private boolean outPortAdded;
    }
}
