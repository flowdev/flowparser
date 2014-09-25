package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.*;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.flowdev.parser.util.ParserUtil;

import java.util.*;

import static org.flowdev.flowparser.util.PortUtil.defaultOutPort;

public class SemanticCreateConnections<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateConnections(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        Flow flow = createFlow(parserData);
        if (ParserUtil.matched(parserData.result())) {
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
                conns.add(connBeg);
            }

            // handle chainMids
            if (chainMids != null) {
                for (Object chainMidObj : chainMids) {
                    List<Object> chainMid = (List<Object>) chainMidObj;
                    String arrowType = (String) chainMid.get(0);
                    PortData fromPort = addOpResult.outPortPair.outPort();
                    Operation toOp = (Operation) chainMid.get(1);
                    PortData toPort = toOp.ports().get(0).inPort();
                    addLastOp(ops, toOp, parserData, addOpResult);
                    toOp = addOpResult.op;

                    Connection connMid = new Connection().dataType(arrowType).showDataType(arrowType != null)
                            .fromOp(lastOp).fromPort(fromPort).toOp(toOp).toPort(toPort);
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
                if (chainEnd.fromPort() == null && chainEnd.toPort() == null) {
                    chainEnd.fromPort(defaultOutPort());
                    chainEnd.toPort(chainEnd.fromPort());
                } else if (chainEnd.toPort() == null) {
                    chainEnd.toPort(chainEnd.fromPort());
                } else if (chainEnd.fromPort() == null) {
                    chainEnd.fromPort(chainEnd.toPort());
                    addOutPort(lastOp, chainEnd.fromPort(), parserData, addOpResult);
                }
                conns.add(chainEnd);
            } else if (addOpResult.outPortAdded) {
                addOpResult.outPortPair.outPort(null);
            }
        }

        Flow result = new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
        verifyFlow(result, parserData);
        return new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
    }

    private void verifyFlow(Flow result, ParserData parserData) {
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
            }
            addPortPair(existingOp, op.ports().get(0), parserData, result);
            result.op = existingOp;
        } else {
            PortPair portPair = op.ports().get(0);
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
        if (newPort == null) {
            return;
        }

        for (PortPair oldPort : op.ports()) {
            if (oldPort.inPort() == null) {
                oldPort.inPort(newPort);
                return;
            } else if (oldPort.inPort().name().equals(newPort.name())) {
                if (oldPort.inPort().hasIndex() == newPort.hasIndex()) {
                    if (!newPort.hasIndex() || oldPort.inPort().index() == newPort.index()) {
                        return;
                    }
                } else {
                    ParserUtil.addError(parserData, "The input port '" + newPort.name() +
                            "' of the operation '" + op.name() + "' is used as indexed and unindexed port in the same flow!");
                    return;
                }
            }
        }

        PortPair newPair = new PortPair().inPort(newPort);
        op.ports().add(newPair);
    }

    private void addOutPort(Operation op, PortData newPort, ParserData parserData, AddOpResult result) {
        if (newPort == null) {
            return;
        }

        for (PortPair oldPort : op.ports()) {
            if (oldPort.outPort() == null) {
                oldPort.outPort(newPort);
                result.outPortPair = oldPort;
                result.outPortAdded = true;
                return;
            } else if (oldPort.outPort().name().equals(newPort.name())) {
                if (oldPort.outPort().hasIndex() == newPort.hasIndex()) {
                    if (!newPort.hasIndex() || oldPort.outPort().index() == newPort.index()) {
                        result.outPortPair = oldPort;
                        return;
                    }
                } else {
                    ParserUtil.addError(parserData, "The output port '" + newPort.name() +
                            "' of the operation '" + op.name() + "' is used as indexed and unindexed port in parallel!");
                    return;
                }
            }
        }

        PortPair newPair = new PortPair().outPort(newPort);
        op.ports().add(newPair);
        result.outPortPair = newPair;
        result.outPortAdded = true;
    }

    private void correctIsLast(Operation op) {
        int n = op.ports().size();
        if (n > 1) {
            op.ports().get(n - 2).isLast(false);
        }
        op.ports().get(n - 1).isLast(true);
    }

    private static class AddOpResult {
        private Operation op;
        private PortPair outPortPair;
        private boolean outPortAdded;
    }
}
