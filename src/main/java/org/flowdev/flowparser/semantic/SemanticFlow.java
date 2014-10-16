package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticFlow<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticFlow(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createFlowFile(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Flow createFlowFile(ParserData parserData) {
        Flow flow = (Flow) parserData.subResults().get(6).value();
        return flow.name(parserData.subResults().get(2).text());
    }
}
