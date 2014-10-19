package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticFlow extends FilterOp<MainData, NoConfig> {
    private final ParserParams<MainData> params;

    public SemanticFlow(ParserParams<MainData> params) {
        this.params = params;
    }

    @Override
    protected void filter(MainData data) {
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
