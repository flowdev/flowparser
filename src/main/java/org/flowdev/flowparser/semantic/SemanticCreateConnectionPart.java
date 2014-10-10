package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.flowparser.util.PortUtil.*;

public class SemanticCreateConnectionPart<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateConnectionPart(ParserParams<T> params) {
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
        PortData inPort = (PortData) parserData.subResults().get(0).value();
        Operation op = (Operation) parserData.subResults().get(1).value();
        PortData outPort = (PortData) parserData.subResults().get(2).value();
        PortPair portPair = new PortPair();

        if (inPort == null) {
            portPair.inPort(defaultInPort());
        } else {
            portPair.inPort(inPort);
        }

        if (outPort == null) {
            portPair.outPort(defaultOutPort(parserData.subResults().get(2).pos()));
        } else {
            portPair.outPort(outPort);
        }

        return op.ports(makePorts(portPair));
    }
}
