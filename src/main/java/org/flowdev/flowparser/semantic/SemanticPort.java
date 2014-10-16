package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;

import static org.flowdev.flowparser.util.PortUtil.newPort;

public class SemanticPort<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticPort(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createPort(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private PortData createPort(ParserData parserData) {
        ParseResult nameResult = parserData.subResults().get(0);
        PortData port = newPort(nameResult.pos(), nameResult.text());

        List<Object> opPortNum = (List<Object>) parserData.subResults().get(1).value();
        if (opPortNum != null) {
            port.hasIndex(true).index(((Long) opPortNum.get(1)).intValue());
        }
        return port;
    }
}
