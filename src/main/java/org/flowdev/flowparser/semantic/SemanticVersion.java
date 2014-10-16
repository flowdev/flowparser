package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;


public class SemanticVersion<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticVersion(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createVersion(parserData.subResults()));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Version createVersion(List<ParseResult> subResults) {
        Long political = (Long) subResults.get(3).value();
        Long major = (Long) subResults.get(5).value();

        return new Version().political(political.intValue()).major(major.intValue());
    }
}
