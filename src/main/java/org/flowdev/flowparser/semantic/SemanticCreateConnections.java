package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.flowdev.flowparser.util.PortUtil.*;

public class SemanticCreateConnections<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateConnections(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createFlow(parserData));

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
            lastOp = addLastOp(ops, (Operation) chainBeg.get(1));
            lastPortPair = lastOp.ports().get(0);

            // handle chainMids
            for (Object chainMidObj : chainMids) {
                List<Object> chainMid = (List<Object>) chainMidObj;
                String arrowType = (String) chainMid.get(0);
                PortPair fromPort = lastPortPair;
                Operation toOp = (Operation) chainMid.get(1);

                Connection connMid = new Connection().dataType(arrowType).showDataType(arrowType != null)
                        .fromOp(lastOp.name()).toOp(toOp.name());
                copyPortOut2From(fromPort, connMid);
                copyPortIn2To(toOp.ports().get(0), connMid);
                conns.add(connMid);

                lastOp = addLastOp(ops, toOp);
                lastPortPair = lastOp.ports().get(0);
            }

            // handle chainEnd
            if (chainEnd != null) {
                copyPortOut2From(lastPortPair, chainEnd);
                if (chainEnd.toPort() == null) {
                    copyPortFrom2To(chainEnd);
                }
                conns.add(chainEnd);
            }
        }

        return new Flow().connections(new ArrayList<>(conns)).operations(new ArrayList<>(ops.values()));
    }

    private Operation addLastOp(Map<String, Operation> ops, Operation op) {
        Operation existingOp = ops.get(op.name());
        if (existingOp != null) {
            if (existingOp.type() == null) {
                existingOp.type(op.type());
            }
            existingOp.ports().add(op.ports().get(0));
        } else {
            ops.put(op.name(), op);
        }
        return op;
    }
}
