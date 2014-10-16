package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticFlow;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseLiteral;
import org.flowdev.parser.op.ParseSpace;
import org.flowdev.parser.op.ParserParams;

public class ParseFlow<T> implements Filter<T, NoConfig> {
    private ParseAll<T> flow;
    private SemanticFlow<T> semantic;
    private ParseLiteral<T> flowLiteral;
    private ParseSpace<T> aspc;
    private ParseBigIdent<T> name;
    private ParseSpaceComment<T> spcComm1;
    private ParseLiteral<T> openFlow;
    private ParseSpaceComment<T> spcComm2;
    private ParseConnections<T> connections;
    private ParseLiteral<T> closeFlow;
    private ParseSpaceComment<T> spcComm3;

    public ParseFlow(ParserParams<T> params) {
        flow = new ParseAll<>(params);
        semantic = new SemanticFlow<>(params);
        flowLiteral = new ParseLiteral<>(params);
        aspc = new ParseSpace<>(params);
        name = new ParseBigIdent<>(params);
        spcComm1 = new ParseSpaceComment<>(params);
        openFlow = new ParseLiteral<>(params);
        spcComm2 = new ParseSpaceComment<>(params);
        connections = new ParseConnections<>(params);
        closeFlow = new ParseLiteral<>(params);
        spcComm3 = new ParseSpaceComment<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        flow.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(flow.getSemInPort());
        flow.setSubOutPort(0, flowLiteral.getInPort());
        flowLiteral.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(1, aspc.getInPort());
        aspc.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(2, name.getInPort());
        name.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(3, spcComm1.getInPort());
        spcComm1.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(4, openFlow.getInPort());
        openFlow.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(5, spcComm2.getInPort());
        spcComm2.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(6, connections.getInPort());
        connections.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(7, closeFlow.getInPort());
        closeFlow.setOutPort(flow.getSubInPort());
        flow.setSubOutPort(8, spcComm3.getInPort());
        spcComm3.setOutPort(flow.getSubInPort());
    }

    private void initConfig() {
        flowLiteral.getConfigPort().send(new ParseLiteral.ParseLiteralConfig().literal("flow"));
        aspc.getConfigPort().send(new ParseSpace.ParseSpaceConfig().acceptNewline(false));
        openFlow.getConfigPort().send(new ParseLiteral.ParseLiteralConfig().literal("{"));
        closeFlow.getConfigPort().send(new ParseLiteral.ParseLiteralConfig().literal("}"));
    }

    public Port<T> getInPort() {
        return flow.getInPort();
    }

    public void setOutPort(Port<T> port) {
        flow.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        flow.setErrorPort(port);
        semantic.setErrorPort(port);
        flowLiteral.setErrorPort(port);
        aspc.setErrorPort(port);
        name.setErrorPort(port);
        spcComm1.setErrorPort(port);
        openFlow.setErrorPort(port);
        spcComm2.setErrorPort(port);
        connections.setErrorPort(port);
        closeFlow.setErrorPort(port);
        spcComm3.setErrorPort(port);
    }
}
