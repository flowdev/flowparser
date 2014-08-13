package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParserParams;

public class ParseConnectionPart<T> implements Filter<T, NoConfig> {
    private ParseAll<T> connPart;
    private SemanticCreateConnectionPart<T> semantic;
    private ParseOptPortSpc<T> optInPort;
    private ParseOperationNameParens<T> opNameParens;
    private ParseOptPort<T> optOutPort;

    public ParseConnectionPart(ParserParams<T> params) {
        optOutPort = new ParseOptPort<>(params);
        optInPort = new ParseOptPortSpc<>(params);
        semantic = new SemanticCreateConnectionPart<>(params);
        opNameParens = new ParseOperationNameParens<>(params);
        connPart = new ParseAll<>(params);

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

    public Port<T> getInPort() {
        return connPart.getInPort();
    }

    public void setOutPort(Port<T> port) {
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
