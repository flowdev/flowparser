package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.flowparser.util.PortUtil.defaultInPort;
import static org.flowdev.flowparser.util.PortUtil.defaultOutPort;

public class SemanticConnectionPart<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticConnectionPart(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createConnectionPart(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Operation createConnectionPart(ParserData parserData) {
        ParseResult inPortResult = parserData.subResults().get(0);
        PortData inPort = (PortData) inPortResult.value();
        Operation op = (Operation) parserData.subResults().get(1).value();
        ParseResult outPortResult = parserData.subResults().get(2);
        PortData outPort = (PortData) outPortResult.value();

        if (inPort == null) {
            op.inPorts().add(defaultInPort(inPortResult.pos()));
        } else {
            op.inPorts().add(inPort);
        }

        if (outPort == null) {
            op.outPorts().add(defaultOutPort(outPortResult.pos()));
        } else {
            op.outPorts().add(outPort);
        }

        return op;
    }
}
