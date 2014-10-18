package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.connections.SemanticConnections;
import org.flowdev.parser.op.*;

/**
 * text input:
 * ( optInPort  [OptDataType]-> optInPort )? opName(OpType) optOutPort
 * ( [OptDataType]-> optInPort opName(OpType) optOutPort )*
 * ( [OptDataType]-> optOutPort )?
 * <p>
 * semantic input:
 * List(multiple1)[
 * List(all)[
 * List(chainBeg)[Connection, Operation], List(multiple0)[ List(chainMid)[arrow, Operation] ], Connection ]
 * ]
 * ]
 * <p>
 * semantic result:
 * Flow
 */
@SuppressWarnings("CanBeFinal")
public class ParseConnections implements Filter<MainData, NoConfig> {
    private ParseMultiple1<MainData> connections;
    //    private SemanticConnections<T> semantic;
    private SemanticConnections semantic;
    private ParseAll<MainData> chain;
    private ParseChainBegin<MainData> chainBeg;
    private ParseMultiple0<MainData> chainMids;
    private ParseOptional<MainData> optChainEnd;
    private ParseStatementEnd<MainData> statementEnd;
    private ParseChainMiddle<MainData> chainMid;
    private ParseChainEnd<MainData> chainEnd;

    public ParseConnections(ParserParams<MainData> params) {
        connections = new ParseMultiple1<>(params);
//        semantic = new SemanticConnections<>(params);
        semantic = new SemanticConnections();
        chain = new ParseAll<>(params);
        chainBeg = new ParseChainBegin<>(params);
        chainMids = new ParseMultiple0<>(params);
        optChainEnd = new ParseOptional<>(params);
        statementEnd = new ParseStatementEnd<>(params);
        chainMid = new ParseChainMiddle<>(params);
        chainEnd = new ParseChainEnd<>(params);

        createConnections();
    }

    private void createConnections() {
        connections.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(connections.getSemInPort());
        connections.setSubOutPort(chain.getInPort());
        chain.setOutPort(connections.getSubInPort());
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

    public Port<MainData> getInPort() {
        return connections.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
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
