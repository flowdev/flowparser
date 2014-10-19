package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticPortSpc;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParseSpace;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseSpace.ParseSpaceConfig;

public class ParseOptPortSpc implements Filter<MainData, NoConfig> {
    private ParseOptional<MainData> optPortSpc;
    private SemanticPortSpc semantic;
    private ParseAll<MainData> portSpc;
    private ParsePort port;
    private ParseSpace<MainData> space;

    public ParseOptPortSpc(ParserParams<MainData> params) {
        optPortSpc = new ParseOptional<>(params);
        semantic = new SemanticPortSpc(params);
        portSpc = new ParseAll<>(params);
        port = new ParsePort(params);
        space = new ParseSpace<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        optPortSpc.setSubOutPort(portSpc.getInPort());
        portSpc.setOutPort(optPortSpc.getSubInPort());
        portSpc.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(portSpc.getSemInPort());
        portSpc.setSubOutPort(0, port.getInPort());
        port.setOutPort(portSpc.getSubInPort());
        portSpc.setSubOutPort(1, space.getInPort());
        space.setOutPort(portSpc.getSubInPort());
    }

    private void initConfig() {
        space.getConfigPort().send(new ParseSpaceConfig().acceptNewline(false));
    }

    public Port<MainData> getInPort() {
        return optPortSpc.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        optPortSpc.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        optPortSpc.setErrorPort(port);
        semantic.setErrorPort(port);
        portSpc.setErrorPort(port);
        this.port.setErrorPort(port);
        space.setErrorPort(port);
    }
}
