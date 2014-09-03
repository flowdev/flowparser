package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticCreateChainBeginMax;
import org.flowdev.flowparser.semantic.SemanticCreateChainBeginMin;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseAlternatives;
import org.flowdev.parser.op.ParserParams;

/**
 * This flow has two alternative inputs and semantic results:
 * maximum case:
 * input:  optInPort  [OptDataType]-> optInPort opName(OpType) optOutPort
 * result: List[ Connection, Operation ]
 * <p>
 * minimum case:
 * input:  opName(OpType) optOutPort
 * result: List[ null, Operation ]
 *
 * @param <T> the type of the data moving through the flow.
 */
public class ParseChainBegin<T> implements Filter<T, NoConfig> {
    private ParseAlternatives<T> chainBeg;
    private ParseAll<T> chainBegMax;
    private SemanticCreateChainBeginMax<T> maxSemantic;
    private ParseOptPort<T> optPortMax;
    private ParseChainMiddle<T> chainMid;
    private ParseAll<T> chainBegMin;
    private SemanticCreateChainBeginMin<T> minSemantic;
    private ParseOperationNameParens<T> opNameParens;
    private ParseOptPort<T> optPortMin;

    public ParseChainBegin(ParserParams<T> params) {
        chainBeg = new ParseAlternatives<>(params);
        chainBegMax = new ParseAll<>(params);
        maxSemantic = new SemanticCreateChainBeginMax<>(params);
        optPortMax = new ParseOptPort<>(params);
        chainMid = new ParseChainMiddle<>(params);
        chainBegMin = new ParseAll<>(params);
        minSemantic = new SemanticCreateChainBeginMin<>(params);
        opNameParens = new ParseOperationNameParens<>(params);
        optPortMin = new ParseOptPort<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        chainBeg.setSubOutPort(0, chainBegMax.getInPort());
        chainBegMax.setOutPort(chainBeg.getSubInPort());
        chainBeg.setSubOutPort(1, chainBegMin.getInPort());
        chainBegMin.setOutPort(chainBeg.getSubInPort());
        chainBegMax.setSemOutPort(maxSemantic.getInPort());
        maxSemantic.setOutPort(chainBegMax.getSemInPort());
        chainBegMax.setSubOutPort(0, optPortMax.getInPort());
        optPortMax.setOutPort(chainBegMax.getSubInPort());
        chainBegMax.setSubOutPort(1, chainMid.getInPort());
        chainMid.setOutPort(chainBegMax.getSubInPort());
        chainBegMin.setSemOutPort(minSemantic.getInPort());
        minSemantic.setOutPort(chainBegMin.getSemInPort());
        chainBegMin.setSubOutPort(0, opNameParens.getInPort());
        opNameParens.setOutPort(chainBegMin.getSubInPort());
        chainBegMin.setSubOutPort(1, optPortMin.getInPort());
        optPortMin.setOutPort(chainBegMin.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return chainBeg.getInPort();
    }

    public void setOutPort(Port<T> port) {
        chainBeg.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        chainBeg.setErrorPort(port);
        chainBegMax.setErrorPort(port);
        maxSemantic.setErrorPort(port);
        optPortMax.setErrorPort(port);
        chainMid.setErrorPort(port);
        chainBegMin.setErrorPort(port);
        minSemantic.setErrorPort(port);
        opNameParens.setErrorPort(port);
        optPortMin.setErrorPort(port);
    }
}
