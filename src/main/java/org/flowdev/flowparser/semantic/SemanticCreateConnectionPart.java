package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.flowparser.util.PortUtil.copyPortIn2Out;
import static org.flowdev.flowparser.util.PortUtil.makePorts;

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
        PortPair inPort = (PortPair) parserData.subResults().get(0).value();
        Operation op = (Operation) parserData.subResults().get(1).value();
        PortPair outPort = (PortPair) parserData.subResults().get(2).value();

        if (inPort == null) {
            inPort = new PortPair().inPort("in").hasInPortIndex(false);
        }
        if (outPort == null) {
            inPort.outPort("out").hasOutPortIndex(false);
        } else {
            copyPortIn2Out(outPort, inPort);
        }
        return op.ports(makePorts(inPort));
    }
}