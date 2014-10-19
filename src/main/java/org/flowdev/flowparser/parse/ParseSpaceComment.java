package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.UseTextSemanticConfig;
import org.flowdev.parser.op.*;

import static org.flowdev.parser.op.ParseBlockComment.ParseBlockCommentConfig;
import static org.flowdev.parser.op.ParseLineComment.ParseLineCommentConfig;
import static org.flowdev.parser.op.ParseSpace.ParseSpaceConfig;

public class ParseSpaceComment implements Filter<MainData, NoConfig> {
    private ParseMultiple0<MainData> spcComs;
    private ParseAlternatives<MainData> spcOrCom;
    private ParseSpace<MainData> space;
    private ParseLineComment<MainData> lineComment;
    private ParseBlockComment<MainData> blockComment;

    public ParseSpaceComment(ParserParams<MainData> params) {
        spcComs = new ParseMultiple0<>(params);
        spcOrCom = new ParseAlternatives<>(params);
        space = new ParseSpace<>(params);
        lineComment = new ParseLineComment<>(params);
        blockComment = new ParseBlockComment<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        spcComs.setSubOutPort(spcOrCom.getInPort());
        spcOrCom.setOutPort(spcComs.getSubInPort());
        spcOrCom.setSubOutPort(0, space.getInPort());
        space.setOutPort(spcOrCom.getSubInPort());
        spcOrCom.setSubOutPort(1, lineComment.getInPort());
        lineComment.setOutPort(spcOrCom.getSubInPort());
        spcOrCom.setSubOutPort(2, blockComment.getInPort());
        blockComment.setOutPort(spcOrCom.getSubInPort());
    }

    private void initConfig() {
        spcComs.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(true));
        spcOrCom.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(true));
        space.getConfigPort().send(new ParseSpaceConfig().acceptNewline(true));
        lineComment.getConfigPort().send(new ParseLineCommentConfig().commentStart("//"));
        blockComment.getConfigPort().send(new ParseBlockCommentConfig().commentStart("/*").commentEnd("*/"));
    }

    public Port<MainData> getInPort() {
        return spcComs.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        spcComs.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        spcComs.setErrorPort(port);
        spcOrCom.setErrorPort(port);
        space.setErrorPort(port);
        lineComment.setErrorPort(port);
        blockComment.setErrorPort(port);
    }
}
