package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticPort;
import org.flowdev.parser.op.*;

import static org.flowdev.parser.op.ParseLiteral.ParseLiteralConfig;

public class ParsePort implements Filter<MainData, NoConfig> {
    private ParseAll<MainData> port;
    private SemanticPort semantic;
    private ParseSmallIdent portName;
    private ParseOptional<MainData> optPortNum;
    private ParseAll<MainData> portNum;
    private ParseLiteral<MainData> dot;
    private ParseNatural<MainData> num;

    public ParsePort(ParserParams<MainData> params) {
        port = new ParseAll<>(params);
        semantic = new SemanticPort(params);
        portName = new ParseSmallIdent(params);
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

    public Port<MainData> getInPort() {
        return port.getInPort();
    }

    public void setOutPort(Port<MainData> outPort) {
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
