package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;


public class SemanticVersion extends FilterOp<MainData, NoConfig> {
    private final ParserParams<MainData> params;

    public SemanticVersion(ParserParams<MainData> params) {
        this.params = params;
    }

    @Override
    protected void filter(MainData data) {
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
