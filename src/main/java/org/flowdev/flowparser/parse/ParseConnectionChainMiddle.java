package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParserParams;

public class ParseConnectionChainMiddle<T> implements Filter<T, NoConfig> {
    private ParseAll<T> chainMid;
    private ParseArrow<T> arrow;
    private ParseConnectionPart<T> connPart;

    public ParseConnectionChainMiddle(ParserParams<T> params) {
        chainMid = new ParseAll<>(params);
        arrow = new ParseArrow<>(params);
        connPart = new ParseConnectionPart<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        chainMid.setSubOutPort(0, arrow.getInPort());
        arrow.setOutPort(chainMid.getSubInPort());
        chainMid.setSubOutPort(1, connPart.getInPort());
        connPart.setOutPort(chainMid.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return chainMid.getInPort();
    }

    public void setOutPort(Port<T> port) {
        chainMid.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        chainMid.setErrorPort(port);
        arrow.setErrorPort(port);
        connPart.setErrorPort(port);
    }
}
