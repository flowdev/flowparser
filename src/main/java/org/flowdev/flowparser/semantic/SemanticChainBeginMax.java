package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParseResult;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.ArrayList;
import java.util.List;

import static org.flowdev.flowparser.util.PortUtil.copyPort;

public class SemanticChainBeginMax<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticChainBeginMax(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createChainBegin(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private List<Object> createChainBegin(ParserData parserData) {
        List<Object> chainBegin = new ArrayList<>(2);
        ParseResult portResult = parserData.subResults().get(0);
        PortData port = (PortData) portResult.value();
        List<Object> list = (List<Object>) parserData.subResults().get(1).value();
        String dataType = (String) list.get(0);
        Operation op = (Operation) list.get(1);
        Connection conn = new Connection();

        if (port == null) {
            port = copyPort(op.inPorts().get(0), portResult.pos());
        }
        conn.fromPort(port).dataType(dataType).showDataType(dataType != null);
        port = op.inPorts().get(0);
        conn.toPort(port).toOp(op);

        chainBegin.add(conn);
        chainBegin.add(op);
        return chainBegin;
    }
}
