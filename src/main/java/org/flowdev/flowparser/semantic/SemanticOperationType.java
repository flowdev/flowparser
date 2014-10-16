package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;


public class SemanticOperationType<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticOperationType(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(parserData.subResults().get(0).value());

        outPort.send(params.setParserData.set(data, parserData));
    }
}
