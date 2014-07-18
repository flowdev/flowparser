package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.parser.op.*;

import static org.flowdev.parser.op.BaseParser.Params;

@SuppressWarnings("WeakerAccess")
public class SpaceComment<T> {
    private ParseMultiple0<T> spcComs;
    private ParseAlternatives<T> spcOrCom;
    private ParseSpace<T> parseSpace;
    private ParseLineComment<T> parseLineComment;
    private ParseBlockComment<T> parseBlockComment;

    public SpaceComment(Params<T> params) {
        spcComs = new ParseMultiple0<>(params);
        spcOrCom = new ParseAlternatives<>(params);
        parseSpace = new ParseSpace<>(params);
        parseLineComment = new ParseLineComment<>(params);
        parseBlockComment = new ParseBlockComment<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        spcComs.setSubOutPort(spcOrCom.getInPort());
        spcOrCom.setOutPort(spcComs.getSubInPort());
        spcOrCom.setSubOutPort(0, parseSpace.getInPort());
        parseSpace.setOutPort(spcOrCom.getSubInPort());
        spcOrCom.setSubOutPort(1, parseLineComment.getInPort());
        parseLineComment.setOutPort(spcOrCom.getSubInPort());
        spcOrCom.setSubOutPort(2, parseBlockComment.getInPort());
        parseBlockComment.setOutPort(spcOrCom.getSubInPort());
    }

    private void initConfig() {
        ParseSpace.ParseSpaceConfig parseSpaceConfig = new ParseSpace.ParseSpaceConfig(false);
        parseSpace.getConfigPort().send(parseSpaceConfig);
    }

    public Port<T> getInPort() {
        return spcComs.getInPort();
    }

    public void setOutPort(Port<T> port) {
        spcComs.setOutPort(port);
    }
}
