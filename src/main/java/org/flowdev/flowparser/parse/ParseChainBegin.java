package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticChainBeginMax;
import org.flowdev.flowparser.semantic.SemanticChainBeginMin;
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
 */
public class ParseChainBegin implements Filter<MainData, NoConfig> {
    private ParseAlternatives<MainData> chainBeg;
    private ParseAll<MainData> chainBegMax;
    private SemanticChainBeginMax maxSemantic;
    private ParseOptPort optPortMax;
    private ParseChainMiddle chainMid;
    private ParseAll<MainData> chainBegMin;
    private SemanticChainBeginMin minSemantic;
    private ParseOperationNameParens opNameParens;
    private ParseOptPort optPortMin;

    public ParseChainBegin(ParserParams<MainData> params) {
        chainBeg = new ParseAlternatives<>(params);
        chainBegMax = new ParseAll<>(params);
        maxSemantic = new SemanticChainBeginMax(params);
        optPortMax = new ParseOptPort(params);
        chainMid = new ParseChainMiddle(params);
        chainBegMin = new ParseAll<>(params);
        minSemantic = new SemanticChainBeginMin(params);
        opNameParens = new ParseOperationNameParens(params);
        optPortMin = new ParseOptPort(params);

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

    public Port<MainData> getInPort() {
        return chainBeg.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
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
