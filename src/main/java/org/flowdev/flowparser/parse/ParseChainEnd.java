package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticChainEnd;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParserParams;

public class ParseChainEnd implements Filter<MainData, NoConfig> {
    private ParseAll<MainData> chainEnd;
    private SemanticChainEnd semantic;
    private ParseArrow arrow;
    private ParseOptPort optPort;

    public ParseChainEnd(ParserParams<MainData> params) {
        semantic = new SemanticChainEnd(params);
        arrow = new ParseArrow(params);
        chainEnd = new ParseAll<>(params);
        optPort = new ParseOptPort(params);

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

    public Port<MainData> getInPort() {
        return chainEnd.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
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
