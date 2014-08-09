package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticCreateFlowFile<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateFlowFile(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createFlowFile(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    private FlowFile createFlowFile(ParserData parserData) {
        return new FlowFile().fileName(parserData.source().name())
                .version((Version) parserData.subResults().get(0).value());
    }
}
