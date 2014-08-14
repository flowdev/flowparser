package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticCreateFlow<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateFlow(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createFlowFile(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    private Flow createFlowFile(ParserData parserData) {
        return new Flow().name(parserData.subResults().get(2).text());
    }
}
