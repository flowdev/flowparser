package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticConnections;
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
 *
 * @param <T> the type of the data moving through the flow.
 */
@SuppressWarnings("CanBeFinal")
public class ParseConnections<T> implements Filter<T, NoConfig> {
    private ParseMultiple1<T> connections;
    private SemanticConnections<T> semantic;
    private ParseAll<T> chain;
    private ParseChainBegin<T> chainBeg;
    private ParseMultiple0<T> chainMids;
    private ParseOptional<T> optChainEnd;
    private ParseStatementEnd<T> statementEnd;
    private ParseChainMiddle<T> chainMid;
    private ParseChainEnd<T> chainEnd;

    public ParseConnections(ParserParams<T> params) {
        connections = new ParseMultiple1<>(params);
        semantic = new SemanticConnections<>(params);
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
