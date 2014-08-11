package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParseSpace;
import org.flowdev.parser.op.ParserParams;

public class ParseOpPortSpc<T> implements Filter<T, NoConfig> {
    private ParseOptional<T> opPortSpc;
    private SemanticCreatePortSpc<T> semanticCreatePortSpc;
    private ParseAll<T> portSpc;
    private ParsePort<T> port;
    private ParseSpace<T> space;

    public ParseOpPortSpc(ParserParams<T> params) {
        opPortSpc = new ParseOptional<>(params);
        semanticCreatePortSpc = new SemanticCreatePortSpc<>(params);
        portSpc = new ParseAll<>(params);
        port = new ParsePort<>(params);
        space = new ParseSpace<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        opPortSpc.setSubOutPort(portSpc.getInPort());
        portSpc.setOutPort(opPortSpc.getSubInPort());
        portSpc.setSemOutPort(semanticCreatePortSpc.getInPort());
        semanticCreatePortSpc.setOutPort(portSpc.getSemInPort());
        portSpc.setSubOutPort(0, port.getInPort());
        port.setOutPort(portSpc.getSubInPort());
        portSpc.setSubOutPort(1, space.getInPort());
        space.setOutPort(portSpc.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return opPortSpc.getInPort();
    }

    public void setOutPort(Port<T> port) {
        opPortSpc.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        opPortSpc.setErrorPort(port);
        semanticCreatePortSpc.setErrorPort(port);
        portSpc.setErrorPort(port);
        this.port.setErrorPort(port);
        space.setErrorPort(port);
    }
}
