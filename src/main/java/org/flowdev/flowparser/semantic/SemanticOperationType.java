package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;


public class SemanticOperationType extends FilterOp<MainData, NoConfig> {
    private final ParserParams<MainData> params;

    public SemanticOperationType(ParserParams<MainData> params) {
        this.params = params;
    }

    @Override
    protected void filter(MainData data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(parserData.subResults().get(0).value());

        outPort.send(params.setParserData.set(data, parserData));
    }
}
