package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticCreateChainBeginMax;
import org.flowdev.flowparser.semantic.SemanticCreateChainBeginMin;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseAlternatives;
import org.flowdev.parser.op.ParserParams;

public class ParseChainBegin<T> implements Filter<T, NoConfig> {
    private ParseAlternatives<T> chainBeg;
    private ParseAll<T> chainBegMax;
    private SemanticCreateChainBeginMax<T> maxSemantic;
    private ParseOptPort<T> opPortMax;
    private ParseChainMiddle<T> chainMid;
    private ParseAll<T> chainBegMin;
    private SemanticCreateChainBeginMin<T> minSemantic;
    private ParseOperationNameParens<T> opNameParens;
    private ParseOptPort<T> opPortMin;

    public ParseChainBegin(ParserParams<T> params) {
        chainBeg = new ParseAlternatives<>(params);
        chainBegMax = new ParseAll<>(params);
        maxSemantic = new SemanticCreateChainBeginMax<>(params);
        opPortMax = new ParseOptPort<>(params);
        chainMid = new ParseChainMiddle<>(params);
        chainBegMin = new ParseAll<>(params);
        minSemantic = new SemanticCreateChainBeginMin<>(params);
        opNameParens = new ParseOperationNameParens<>(params);
        opPortMin = new ParseOptPort<>(params);

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
        chainBegMax.setSubOutPort(0, opPortMax.getInPort());
        opPortMax.setOutPort(chainBegMax.getSubInPort());
        chainBegMax.setSubOutPort(1, chainMid.getInPort());
        chainMid.setOutPort(chainBegMax.getSubInPort());
        chainBegMin.setSemOutPort(minSemantic.getInPort());
        minSemantic.setOutPort(chainBegMin.getSemInPort());
        chainBegMin.setSubOutPort(0, opNameParens.getInPort());
        opNameParens.setOutPort(chainBegMin.getSubInPort());
        chainBegMin.setSubOutPort(1, opPortMin.getInPort());
        opPortMin.setOutPort(chainBegMin.getSubInPort());
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
        opPortMax.setErrorPort(port);
        chainMid.setErrorPort(port);
        chainBegMin.setErrorPort(port);
        minSemantic.setErrorPort(port);
        opNameParens.setErrorPort(port);
        opPortMin.setErrorPort(port);
    }
}
