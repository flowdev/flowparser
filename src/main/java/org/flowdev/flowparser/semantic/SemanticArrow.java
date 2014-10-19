package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;

public class SemanticArrow extends FilterOp<MainData, NoConfig> {
    private final ParserParams<MainData> params;

    public SemanticArrow(ParserParams<MainData> params) {
        this.params = params;
    }

    @Override
    protected void filter(MainData data) {
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
