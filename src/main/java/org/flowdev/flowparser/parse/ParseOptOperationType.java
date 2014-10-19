package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticOperationType;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;


public class ParseOptOperationType implements Filter<MainData, NoConfig> {
    private ParseOptional<MainData> optOpType;
    private ParseAll<MainData> opType;
    private SemanticOperationType semantic;
    private ParseBigIdent bigIdent;
    private ParseOptSpc optSpc;

    public ParseOptOperationType(ParserParams<MainData> params) {
        optOpType = new ParseOptional<>(params);
        opType = new ParseAll<>(params);
        semantic = new SemanticOperationType(params);
        bigIdent = new ParseBigIdent(params);
        optSpc = new ParseOptSpc(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        optOpType.setSubOutPort(opType.getInPort());
        opType.setOutPort(optOpType.getSubInPort());
        opType.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(opType.getSemInPort());
        opType.setSubOutPort(0, bigIdent.getInPort());
        bigIdent.setOutPort(opType.getSubInPort());
        opType.setSubOutPort(1, optSpc.getInPort());
        optSpc.setOutPort(opType.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<MainData> getInPort() {
        return optOpType.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        optOpType.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> errorPort) {
        optOpType.setErrorPort(errorPort);
        semantic.setErrorPort(errorPort);
        opType.setErrorPort(errorPort);
        bigIdent.setErrorPort(errorPort);
        optSpc.setErrorPort(errorPort);
    }
}
