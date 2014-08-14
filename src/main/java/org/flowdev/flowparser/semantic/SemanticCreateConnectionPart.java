package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import static java.util.Collections.singletonList;

public class SemanticCreateConnectionPart<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticCreateConnectionPart(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createOperationNameParens(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Operation createOperationNameParens(ParserData parserData) {
        PortPair inPort = (PortPair) parserData.subResults().get(0).value();
        Operation op = (Operation) parserData.subResults().get(1).value();
        PortPair outPort = (PortPair) parserData.subResults().get(2).value();

        if (inPort == null) {
            inPort = new PortPair().inPort("in").hasInPortIndex(false);
        }
        if (outPort == null) {
            inPort.outPort("out").hasOutPortIndex(false);
        } else {
            inPort.outPort(outPort.inPort()).hasOutPortIndex(outPort.hasInPortIndex()).outPortIndex(outPort.inPortIndex());
        }
        return op.ports(singletonList(inPort));
    }
}
