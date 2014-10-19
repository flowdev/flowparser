package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.flowparser.util.PortUtil.newPort;

public class SemanticChainEnd extends FilterOp<MainData, NoConfig> {
    private final ParserParams<MainData> params;

    public SemanticChainEnd(ParserParams<MainData> params) {
        this.params = params;
    }

    @Override
    protected void filter(MainData data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createChainEnd(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Connection createChainEnd(ParserData parserData) {
        Connection conn = new Connection();

        ParseResult arrowResult = parserData.subResults().get(0);
        String dataType = (String) arrowResult.value();
        conn.showDataType(dataType != null).dataType(dataType).fromPort(newPort(arrowResult.pos(), null));

        ParseResult portResult = parserData.subResults().get(1);
        PortData port = (PortData) portResult.value();
        if (port != null) {
            conn.toPort(port);
        } else {
            conn.toPort(newPort(portResult.pos(), null));
        }

        return conn;
    }
}
