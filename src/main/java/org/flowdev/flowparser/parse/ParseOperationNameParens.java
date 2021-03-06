package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticOperationNameParens;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseLiteral;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;

public class ParseOperationNameParens implements Filter<MainData, NoConfig> {
    private ParseAll<MainData> opNameParens;
    private SemanticOperationNameParens semantic;
    private ParseOptional<MainData> optOpName;
    private ParseLiteral<MainData> openType;
    private ParseOptSpc spc1;
    private ParseOptOperationType optOpType;
    private ParseLiteral<MainData> closeType;
    private ParseOptSpc spc2;
    private ParseAll<MainData> opName;
    private ParseSmallIdent smallIdent;
    private ParseOptSpc spc3;

    public ParseOperationNameParens(ParserParams<MainData> params) {
        opNameParens = new ParseAll<>(params);
        semantic = new SemanticOperationNameParens(params);
        optOpName = new ParseOptional<>(params);
        openType = new ParseLiteral<>(params);
        spc1 = new ParseOptSpc(params);
        optOpType = new ParseOptOperationType(params);
        closeType = new ParseLiteral<>(params);
        spc2 = new ParseOptSpc(params);
        opName = new ParseAll<>(params);
        smallIdent = new ParseSmallIdent(params);
        spc3 = new ParseOptSpc(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        opNameParens.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(opNameParens.getSemInPort());
        opNameParens.setSubOutPort(0, optOpName.getInPort());
        optOpName.setOutPort(opNameParens.getSubInPort());
        opNameParens.setSubOutPort(1, openType.getInPort());
        openType.setOutPort(opNameParens.getSubInPort());
        opNameParens.setSubOutPort(2, spc1.getInPort());
        spc1.setOutPort(opNameParens.getSubInPort());
        opNameParens.setSubOutPort(3, optOpType.getInPort());
        optOpType.setOutPort(opNameParens.getSubInPort());
        opNameParens.setSubOutPort(4, closeType.getInPort());
        closeType.setOutPort(opNameParens.getSubInPort());
        opNameParens.setSubOutPort(5, spc2.getInPort());
        spc2.setOutPort(opNameParens.getSubInPort());
        optOpName.setSubOutPort(opName.getInPort());
        opName.setOutPort(optOpName.getSubInPort());
        opName.setSubOutPort(0, smallIdent.getInPort());
        smallIdent.setOutPort(opName.getSubInPort());
        opName.setSubOutPort(1, spc3.getInPort());
        spc3.setOutPort(opName.getSubInPort());
    }

    private void initConfig() {
        openType.getConfigPort().send(new ParseLiteral.ParseLiteralConfig().literal("("));
        closeType.getConfigPort().send(new ParseLiteral.ParseLiteralConfig().literal(")"));
    }

    public Port<MainData> getInPort() {
        return opNameParens.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        opNameParens.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        opNameParens.setErrorPort(port);
        semantic.setErrorPort(port);
        optOpName.setErrorPort(port);
        openType.setErrorPort(port);
        spc1.setErrorPort(port);
        optOpType.setErrorPort(port);
        closeType.setErrorPort(port);
        spc2.setErrorPort(port);
        smallIdent.setErrorPort(port);
        spc3.setErrorPort(port);
    }
}
