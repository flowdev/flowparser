package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticCreateOperationType;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;


public class ParseOptOperationType<T> implements Filter<T, NoConfig> {
    private ParseOptional<T> optOpType;
    private ParseAll<T> opType;
    private SemanticCreateOperationType<T> semantic;
    private ParseBigIdent<T> bigIdent;
    private ParseOptSpc<T> optSpc;

    public ParseOptOperationType(ParserParams<T> params) {
        opType = new ParseAll<>(params);
        bigIdent = new ParseBigIdent<>(params);
        optSpc = new ParseOptSpc<>(params);
        optOpType = new ParseOptional<>(params);
        semantic = new SemanticCreateOperationType<>(params);

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

    public Port<T> getInPort() {
        return optOpType.getInPort();
    }

    public void setOutPort(Port<T> port) {
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
