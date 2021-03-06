package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.ArrayList;
import java.util.List;

import static org.flowdev.flowparser.util.PortUtil.defaultOutPort;

public class SemanticChainBeginMin extends FilterOp<MainData, NoConfig> {
    private final ParserParams<MainData> params;

    public SemanticChainBeginMin(ParserParams<MainData> params) {
        this.params = params;
    }

    @Override
    protected void filter(MainData data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createChainBegin(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private List<Object> createChainBegin(ParserData parserData) {
        List<Object> chainBegin = new ArrayList<>(2);
        Operation op = (Operation) parserData.subResults().get(0).value();
        ParseResult portResult = parserData.subResults().get(1);
        PortData port = (PortData) portResult.value();
        if (port != null) {
            op.outPorts().add(port);
        } else {
            op.outPorts().add(defaultOutPort(portResult.pos()));
        }
        chainBegin.add(null);
        chainBegin.add(op);
        return chainBegin;
    }

}
