package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticCreateChain<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticCreateChain(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createChain(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    private Object createChain(ParserData parserData) {
        return null;
    }
}
