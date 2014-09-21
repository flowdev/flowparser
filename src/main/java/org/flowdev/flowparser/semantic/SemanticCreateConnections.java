package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.*;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.flowdev.parser.util.ParserUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            PortPair lastPortPair;
            List<Object> chain = (List<Object>) subResult.value();
            List<Object> chainBeg = (List<Object>) chain.get(0);
            List<Object> chainMids = (List<Object>) chain.get(1);
            Connection chainEnd = (Connection) chain.get(2);

            // handle chainBeg
            Connection connBeg = (Connection) chainBeg.get(0);
            if (connBeg != null) {
                conns.add(connBeg);
            }
            lastOp = addLastOp(ops, (Operation) chainBeg.get(1), parserData);
            lastPortPair = lastOp.ports().get(0);

            // handle chainMids
            if (chainMids != null) {
                for (Object chainMidObj : chainMids) {
                    List<Object> chainMid = (List<Object>) chainMidObj;
                    String arrowType = (String) chainMid.get(0);
                    PortPair fromPort = lastPortPair;
                    Operation toOp = (Operation) chainMid.get(1);

                    Connection connMid = new Connection().dataType(arrowType).showDataType(arrowType != null)
                            .fromOp(lastOp.name()).toOp(toOp.name())
                            .fromPort(fromPort.outPort()).toPort(toOp.ports().get(0).inPort());
                    conns.add(connMid);

                    lastOp = addLastOp(ops, toOp, parserData);
                    lastPortPair = lastOp.ports().get(0);
                }
            }

            // handle chainEnd
            if (chainEnd != null) {
                chainEnd.fromOp(lastOp.name()).fromPort(lastPortPair.outPort());
                if (chainEnd.toPort() == null) {
                    chainEnd.toPort(chainEnd.fromPort());
                }
                conns.add(chainEnd);
            } else {
                lastPortPair.outPort(null);
            }
        }

        return new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
    }

    private Operation addLastOp(Map<String, Operation> ops, Operation op, ParserData parserData) {
        Operation existingOp = ops.get(op.name());
        if (existingOp != null) {
            if (existingOp.type() == null) {
                existingOp.type(op.type());
            }
            addPortPair(existingOp, op.ports().get(0), parserData);
        } else {
            op.ports().get(0).isLast(true);
            ops.put(op.name(), op);
        }
        return op;
    }

    private void addPortPair(Operation op, PortPair portPair, ParserData parserData) {
        addInPort(op, portPair.inPort(), parserData);
        addOutPort(op, portPair.outPort(), parserData);
        correctIsLast(op);
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
                    if (oldPort.inPort().index() == newPort.index()) {
                        return;
                    }
                } else {
                    ParserUtil.addError(parserData, "The input port '" + newPort.name() +
                            "' of the operation '" + op.name() + "' is used as indexed and unindexed port in the same flow!");
                    return;
                }
            }
        }

        op.ports().add(new PortPair().inPort(newPort));
    }

    private void addOutPort(Operation op, PortData newPort, ParserData parserData) {
        if (newPort == null) {
            return;
        }

        for (PortPair oldPort : op.ports()) {
            if (oldPort.outPort() == null) {
                oldPort.outPort(newPort);
                return;
            } else if (oldPort.outPort().name().equals(newPort.name())) {
                if (oldPort.outPort().hasIndex() == newPort.hasIndex()) {
                    if (oldPort.outPort().index() == newPort.index()) {
                        return;
                    }
                } else {
                    ParserUtil.addError(parserData, "The output port '" + newPort.name() +
                            "' of the operation '" + op.name() + "' is used as indexed and unindexed port in parallel!");
                    return;
                }
            }
        }

        op.ports().add(new PortPair().outPort(newPort));
    }

    private void correctIsLast(Operation op) {
        int n = op.ports().size();
        if (n > 1) {
            op.ports().get(n - 2).isLast(false);
        }
        op.ports().get(n - 1).isLast(true);
    }

}
