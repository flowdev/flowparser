package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticCreateChain;
import org.flowdev.parser.op.*;

public class ParseConnections<T> implements Filter<T, NoConfig> {
    private ParseMultiple1 connections;
    private ParseAll chain;
    private SemanticCreateChain semantic;
    private ParseChainBegin chainBeg;
    private ParseMultiple0 chainMids;
    private ParseOptional optChainEnd;
    private ParseStatementEnd statementEnd;
    private ParseChainMiddle chainMid;
    private ParseChainEnd chainEnd;

    public ParseConnections(ParserParams<T> params) {
        connections = new ParseMultiple1(params);
        chain = new ParseAll(params);
        semantic = new SemanticCreateChain(params);
        chainBeg = new ParseChainBegin(params);
        chainMids = new ParseMultiple0(params);
        optChainEnd = new ParseOptional(params);
        statementEnd = new ParseStatementEnd(params);
        chainMid = new ParseChainMiddle(params);
        chainEnd = new ParseChainEnd(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        connections.setSubOutPort(chain.getInPort());
        chain.setOutPort(connections.getSubInPort());
        chain.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(chain.getSemInPort());
        chain.setSubOutPort(0, chainBeg.getInPort());
        chainBeg.setOutPort(chain.getSubInPort());
        chain.setSubOutPort(1, chainMids.getInPort());
        chainMids.setOutPort(chain.getSubInPort());
        chain.setSubOutPort(2, optChainEnd.getInPort());
        optChainEnd.setOutPort(chain.getSubInPort());
        chain.setSubOutPort(3, statementEnd.getInPort());
        statementEnd.setOutPort(chain.getSubInPort());
        chainMids.setSubOutPort(chainMid.getInPort());
        chainMid.setOutPort(chainMids.getSubInPort());
        optChainEnd.setSubOutPort(chainEnd.getInPort());
        chainEnd.setOutPort(optChainEnd.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return connections.getInPort();
    }

    public void setOutPort(Port<T> port) {
        connections.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        connections.setErrorPort(port);
        chain.setErrorPort(port);
        semantic.setErrorPort(port);
        chainBeg.setErrorPort(port);
        chainMids.setErrorPort(port);
        optChainEnd.setErrorPort(port);
        statementEnd.setErrorPort(port);
        chainMid.setErrorPort(port);
        chainEnd.setErrorPort(port);
    }
}
