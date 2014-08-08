package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;


public class SemanticCreateVersion<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticCreateVersion(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.getResult().setValue(createVersion(parserData.getSubResults()));

        outPort.send(params.setParserData.set(data, parserData));
    }

    private Version createVersion(List<ParseResult> subResults) {
        Long political = (Long) subResults.get(3).getValue();
        Long major = (Long) subResults.get(5).getValue();

        return new Version().political(political.intValue()).major(major.intValue());
    }
}
