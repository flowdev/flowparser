package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;

public class SemanticCreateArrow<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateArrow(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createArrow(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Object createArrow(ParserData parserData) {
        List<Object> opType = (List<Object>) parserData.subResults().get(1).value();
        if (opType == null) {
            return null;
        } else {
            return opType.get(2);
        }
    }
}
