package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseLiteral;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseLiteral.ParseLiteralConfig;

public class ParseArrow<T> implements Filter<T, NoConfig> {
    private ParseAll<T> arrow;
    private SemanticCreateArrow<T> semanticCreateArrow;
    private ParseSpaceComment<T> spcCom1;
    private ParseOptional<T> opType;
    private ParseLiteral<T> litArr;
    private ParseSpaceComment<T> spcCom2;
    private ParseAll<T> type;
    private ParseLiteral<T> openType;
    private ParseOpSpc<T> spc1;
    private ParseBigIdent<T> typeName;
    private ParseOpSpc<T> spc2;
    private ParseLiteral<T> closeType;

    public ParseArrow(ParserParams<T> params) {
        arrow = new ParseAll<>(params);
        semanticCreateArrow = new SemanticCreateArrow<>(params);
        spcCom1 = new ParseSpaceComment<>(params);
        opType = new ParseOptional<>(params);
        litArr = new ParseLiteral<>(params);
        spcCom2 = new ParseSpaceComment<>(params);
        type = new ParseAll<>(params);
        openType = new ParseLiteral<>(params);
        spc1 = new ParseOpSpc<>(params);
        typeName = new ParseBigIdent<>(params);
        spc2 = new ParseOpSpc<>(params);
        closeType = new ParseLiteral<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        arrow.setSemOutPort(semanticCreateArrow.getInPort());
        semanticCreateArrow.setOutPort(arrow.getSemInPort());
        arrow.setSubOutPort(0, spcCom1.getInPort());
        spcCom1.setOutPort(arrow.getSubInPort());
        arrow.setSubOutPort(1, opType.getInPort());
        opType.setOutPort(arrow.getSubInPort());
        arrow.setSubOutPort(2, litArr.getInPort());
        litArr.setOutPort(arrow.getSubInPort());
        arrow.setSubOutPort(3, spcCom2.getInPort());
        spcCom2.setOutPort(arrow.getSubInPort());
        opType.setSubOutPort(type.getInPort());
        type.setOutPort(opType.getSubInPort());
        type.setSubOutPort(0, openType.getInPort());
        openType.setOutPort(type.getSubInPort());
        type.setSubOutPort(1, spc1.getInPort());
        spc1.setOutPort(type.getSubInPort());
        type.setSubOutPort(2, typeName.getInPort());
        typeName.setOutPort(type.getSubInPort());
        type.setSubOutPort(3, spc2.getInPort());
        spc2.setOutPort(type.getSubInPort());
        type.setSubOutPort(4, closeType.getInPort());
        closeType.setOutPort(type.getSubInPort());
    }

    private void initConfig() {
        litArr.getConfigPort().send(new ParseLiteralConfig().literal("->"));
        openType.getConfigPort().send(new ParseLiteralConfig().literal("["));
        closeType.getConfigPort().send(new ParseLiteralConfig().literal("]"));
    }

    public Port<T> getInPort() {
        return arrow.getInPort();
    }

    public void setOutPort(Port<T> port) {
        arrow.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        arrow.setErrorPort(port);
        semanticCreateArrow.setErrorPort(port);
        spcCom1.setErrorPort(port);
        opType.setErrorPort(port);
        litArr.setErrorPort(port);
        spcCom2.setErrorPort(port);
        type.setErrorPort(port);
        openType.setErrorPort(port);
        spc1.setErrorPort(port);
        typeName.setErrorPort(port);
        spc2.setErrorPort(port);
        closeType.setErrorPort(port);
    }
}