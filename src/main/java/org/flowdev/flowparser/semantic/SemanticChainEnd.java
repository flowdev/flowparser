package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticChainEnd<T> extends FilterOp<T, NoConfig> {
    private final ParserParams<T> params;

    public SemanticChainEnd(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createChainEnd(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Connection createChainEnd(ParserData parserData) {
        Connection conn = new Connection();

        ParseResult arrowResult = parserData.subResults().get(0);
        String dataType = (String) arrowResult.value();
        conn.showDataType(dataType != null).dataType(dataType).fromPort(new PortData().srcPos(arrowResult.pos()));

        ParseResult portResult = parserData.subResults().get(1);
        PortData port = (PortData) portResult.value();
        if (port != null) {
            conn.toPort(port);
        } else {
            conn.toPort(new PortData().srcPos(portResult.pos()));
        }

        return conn;
    }
}
