package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.flowparser.util.SemanticConnectionsUtil;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.flowdev.parser.util.ParserUtil;

import java.util.*;

import static org.flowdev.flowparser.util.PortUtil.copyPort;
import static org.flowdev.flowparser.util.PortUtil.defaultOutPort;
import static org.flowdev.parser.util.ParserUtil.addSemanticError;
import static org.flowdev.parser.util.ParserUtil.isOk;

public class SemanticConnections<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticConnections(ParserParams<T> params) {
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
            lastOp = addOpResult.op();
            if (connBeg != null) {
                connBeg.toOp(lastOp);
                SemanticConnectionsUtil.correctToPort(connBeg, lastOp);
                conns.add(connBeg);
            }

            // handle chainMids
            if (chainMids != null) {
                for (Object chainMidObj : chainMids) {
                    List<Object> chainMid = (List<Object>) chainMidObj;
                    String arrowType = (String) chainMid.get(0);
                    PortData fromPort = addOpResult.outPort();
                    Operation toOp = (Operation) chainMid.get(1);
                    PortData toPort = toOp.inPorts().get(0);
                    addLastOp(ops, toOp, parserData, addOpResult);
                    toOp = addOpResult.op();

                    Connection connMid = new Connection().dataType(arrowType).showDataType(arrowType != null)
                            .fromOp(lastOp).fromPort(fromPort).toOp(toOp).toPort(toPort);
                    SemanticConnectionsUtil.correctToPort(connMid, toOp);
                    conns.add(connMid);

                    lastOp = toOp;
                }
            }

            // handle chainEnd
            if (chainEnd != null) {
                chainEnd.fromOp(lastOp);
                if (addOpResult.outPort() != null) {
                    chainEnd.fromPort(addOpResult.outPort());
                }
                if (chainEnd.fromPort().name() == null && chainEnd.toPort().name() == null) {
                    chainEnd.fromPort(defaultOutPort(chainEnd.fromPort().srcPos()));
                    chainEnd.toPort(copyPort(chainEnd.fromPort(), chainEnd.toPort().srcPos()));
                } else if (chainEnd.toPort().name() == null) {
                    chainEnd.toPort(copyPort(chainEnd.fromPort(), chainEnd.toPort().srcPos()));
                } else if (chainEnd.fromPort() == null) {
                    chainEnd.fromPort(defaultOutPort(chainEnd.fromPort().srcPos()));
                    SemanticConnectionsUtil.addPort(lastOp, chainEnd.fromPort(), "output", parserData, addOpResult);
                }
                SemanticConnectionsUtil.correctFromPort(chainEnd, lastOp);
                conns.add(chainEnd);
            } else if (addOpResult.outPortAdded()) {
                addOpResult.op().outPorts().remove(addOpResult.op().outPorts().size() - 1);
            }
        }
        parserData.result().value(null);

        Flow result = new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
        verifyFlow(result, parserData);
        return new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
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
            if (op.inPorts().size() > 0) {
                SemanticConnectionsUtil.addPort(existingOp, op.inPorts().get(0), "input", parserData, null);
            }
            if (op.outPorts().size() > 0) {
                SemanticConnectionsUtil.addPort(existingOp, op.outPorts().get(0), "output", parserData, result);
            }
            result.op(existingOp);
        } else {
            ops.put(op.name(), op);
            if (op.outPorts().size() > 0) {
                result.outPort(op.outPorts().get(0)).outPortAdded(true);
            }
            result.op(op);
        }
    }

}
