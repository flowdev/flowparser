package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

public class SemanticCreateChainEnd<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticCreateChainEnd(ParserParams<T> params) {
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

        String dataType = (String) parserData.subResults().get(0).value();
        conn.showDataType(dataType != null).dataType(dataType);

        PortPair port = (PortPair) parserData.subResults().get(1).value();
        if (port == null) {
            conn.toPort("out").hasToPortIndex(false);
        } else {
            conn.toPort(port.inPort()).hasToPortIndex(port.hasInPortIndex()).toPortIndex(port.inPortIndex());
        }

        return conn;
    }
}
