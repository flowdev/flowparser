package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticChainEnd;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParserParams;

public class ParseChainEnd<T> implements Filter<T, NoConfig> {
    private ParseAll<T> chainEnd;
    private SemanticChainEnd<T> semantic;
    private ParseArrow<T> arrow;
    private ParseOptPort<T> optPort;

    public ParseChainEnd(ParserParams<T> params) {
        semantic = new SemanticChainEnd<>(params);
        arrow = new ParseArrow<>(params);
        chainEnd = new ParseAll<>(params);
        optPort = new ParseOptPort<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        chainEnd.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(chainEnd.getSemInPort());
        chainEnd.setSubOutPort(0, arrow.getInPort());
        arrow.setOutPort(chainEnd.getSubInPort());
        chainEnd.setSubOutPort(1, optPort.getInPort());
        optPort.setOutPort(chainEnd.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return chainEnd.getInPort();
    }

    public void setOutPort(Port<T> port) {
        chainEnd.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        chainEnd.setErrorPort(port);
        semantic.setErrorPort(port);
        arrow.setErrorPort(port);
        optPort.setErrorPort(port);
    }
}
