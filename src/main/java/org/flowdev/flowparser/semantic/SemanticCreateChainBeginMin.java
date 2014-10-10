package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.ArrayList;
import java.util.List;

import static org.flowdev.flowparser.util.PortUtil.defaultOutPort;
import static org.flowdev.flowparser.util.PortUtil.makePorts;

public class SemanticCreateChainBeginMin<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateChainBeginMin(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createChainBegin(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private List<Object> createChainBegin(ParserData parserData) {
        List<Object> chainBegin = new ArrayList<>(2);
        Operation op = (Operation) parserData.subResults().get(0).value();
        ParseResult portResult = parserData.subResults().get(1);
        PortData port = (PortData) portResult.value();
        PortPair portPair = new PortPair();
        if (port != null) {
            portPair.outPort(port);
        } else {
            portPair.outPort(defaultOutPort(portResult.pos()));
        }
        op.ports(makePorts(portPair));
        chainBegin.add(null);
        chainBegin.add(op);
        return chainBegin;
    }

}
