package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticConnectionPart;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParserParams;

public class ParseConnectionPart implements Filter<MainData, NoConfig> {
    private ParseAll<MainData> connPart;
    private SemanticConnectionPart semantic;
    private ParseOptPortSpc optInPort;
    private ParseOperationNameParens opNameParens;
    private ParseOptPort optOutPort;

    public ParseConnectionPart(ParserParams<MainData> params) {
        connPart = new ParseAll<>(params);
        semantic = new SemanticConnectionPart(params);
        optInPort = new ParseOptPortSpc(params);
        opNameParens = new ParseOperationNameParens(params);
        optOutPort = new ParseOptPort(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        connPart.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(connPart.getSemInPort());
        connPart.setSubOutPort(0, optInPort.getInPort());
        optInPort.setOutPort(connPart.getSubInPort());
        connPart.setSubOutPort(1, opNameParens.getInPort());
        opNameParens.setOutPort(connPart.getSubInPort());
        connPart.setSubOutPort(2, optOutPort.getInPort());
        optOutPort.setOutPort(connPart.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<MainData> getInPort() {
        return connPart.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        connPart.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        connPart.setErrorPort(port);
        semantic.setErrorPort(port);
        optInPort.setErrorPort(port);
        opNameParens.setErrorPort(port);
        optOutPort.setErrorPort(port);
    }
}
