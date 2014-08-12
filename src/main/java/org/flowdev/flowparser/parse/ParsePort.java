package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.*;

import static org.flowdev.parser.op.ParseLiteral.ParseLiteralConfig;

public class ParsePort<T> implements Filter<T, NoConfig> {
    private ParseAll<T> port;
    private SemanticCreatePort<T> semantic;
    private ParseSmallIdent<T> portName;
    private ParseOptional<T> optPortNum;
    private ParseAll<T> portNum;
    private ParseLiteral<T> dot;
    private ParseNatural<T> num;

    public ParsePort(ParserParams<T> params) {
        port = new ParseAll<>(params);
        semantic = new SemanticCreatePort<>(params);
        portName = new ParseSmallIdent<>(params);
        optPortNum = new ParseOptional<>(params);
        portNum = new ParseAll<>(params);
        dot = new ParseLiteral<>(params);
        num = new ParseNatural<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        port.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(port.getSemInPort());
        port.setSubOutPort(0, portName.getInPort());
        portName.setOutPort(port.getSubInPort());
        port.setSubOutPort(1, optPortNum.getInPort());
        optPortNum.setOutPort(port.getSubInPort());
        optPortNum.setSubOutPort(portNum.getInPort());
        portNum.setOutPort(optPortNum.getSubInPort());
        portNum.setSubOutPort(0, dot.getInPort());
        dot.setOutPort(portNum.getSubInPort());
        portNum.setSubOutPort(1, num.getInPort());
        num.setOutPort(portNum.getSubInPort());
    }

    private void initConfig() {
        dot.getConfigPort().send(new ParseLiteralConfig().literal("."));
    }

    public Port<T> getInPort() {
        return port.getInPort();
    }

    public void setOutPort(Port<T> outPort) {
        port.setOutPort(outPort);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> errorPort) {
        port.setErrorPort(errorPort);
        semantic.setErrorPort(errorPort);
        portName.setErrorPort(errorPort);
        optPortNum.setErrorPort(errorPort);
        portNum.setErrorPort(errorPort);
        dot.setErrorPort(errorPort);
        num.setErrorPort(errorPort);
    }
}
