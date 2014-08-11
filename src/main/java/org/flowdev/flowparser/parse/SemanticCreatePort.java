package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;

public class SemanticCreatePort<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreatePort(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createPort(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private PortPair createPort(ParserData parserData) {
        PortPair portPair = new PortPair().inPort(parserData.subResults().get(0).text());

        List<Object> opPortNum = (List<Object>) parserData.subResults().get(1).value();
        if (opPortNum == null) {
            portPair.hasInPortIndex(false);
        } else {
            portPair.hasInPortIndex(true).inPortIndex(((Long) opPortNum.get(1)).intValue());
        }
        return portPair;
    }
}
