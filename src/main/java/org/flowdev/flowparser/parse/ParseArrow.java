package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.semantic.SemanticArrow;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseLiteral;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseLiteral.ParseLiteralConfig;

public class ParseArrow implements Filter<MainData, NoConfig> {
    private ParseAll<MainData> arrow;
    private SemanticArrow semantic;
    private ParseSpaceComment spcCom1;
    private ParseOptional<MainData> optType;
    private ParseLiteral<MainData> litArr;
    private ParseSpaceComment spcCom2;
    private ParseAll<MainData> type;
    private ParseLiteral<MainData> openType;
    private ParseOptSpc spc1;
    private ParseBigIdent typeName;
    private ParseOptSpc spc2;
    private ParseLiteral<MainData> closeType;

    public ParseArrow(ParserParams<MainData> params) {
        arrow = new ParseAll<>(params);
        semantic = new SemanticArrow(params);
        spcCom1 = new ParseSpaceComment(params);
        optType = new ParseOptional<>(params);
        litArr = new ParseLiteral<>(params);
        spcCom2 = new ParseSpaceComment(params);
        type = new ParseAll<>(params);
        openType = new ParseLiteral<>(params);
        spc1 = new ParseOptSpc(params);
        typeName = new ParseBigIdent(params);
        spc2 = new ParseOptSpc(params);
        closeType = new ParseLiteral<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        arrow.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(arrow.getSemInPort());
        arrow.setSubOutPort(0, spcCom1.getInPort());
        spcCom1.setOutPort(arrow.getSubInPort());
        arrow.setSubOutPort(1, optType.getInPort());
        optType.setOutPort(arrow.getSubInPort());
        arrow.setSubOutPort(2, litArr.getInPort());
        litArr.setOutPort(arrow.getSubInPort());
        arrow.setSubOutPort(3, spcCom2.getInPort());
        spcCom2.setOutPort(arrow.getSubInPort());
        optType.setSubOutPort(type.getInPort());
        type.setOutPort(optType.getSubInPort());
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

    public Port<MainData> getInPort() {
        return arrow.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        arrow.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        arrow.setErrorPort(port);
        semantic.setErrorPort(port);
        spcCom1.setErrorPort(port);
        optType.setErrorPort(port);
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
