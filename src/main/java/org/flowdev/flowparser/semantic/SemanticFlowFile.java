package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;

public class SemanticFlowFile<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticFlowFile(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createFlowFile(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private FlowFile createFlowFile(ParserData parserData) {
        return new FlowFile().fileName(parserData.source().name())
                .version((Version) parserData.subResults().get(0).value())
                .flows((List<Flow>) parserData.subResults().get(1).value());
    }
}
