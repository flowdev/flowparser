package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseLiteral;

import static org.flowdev.parser.op.BaseParser.Params;

@SuppressWarnings("WeakerAccess")
public class StatementEnd<T> {
    private SpaceComment<T> spaceComment;
    private ParseAll<T> stmtEnd;
    private ParseLiteral<T> parseLiteral;

    public StatementEnd(Params<T> params) {
        spaceComment = new SpaceComment<>(params);
        stmtEnd = new ParseAll<>(params);
        parseLiteral = new ParseLiteral<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        stmtEnd.setSubOutPort(0, spaceComment.getInPort());
        spaceComment.setOutPort(stmtEnd.getSubInPort());
        stmtEnd.setSubOutPort(1, parseLiteral.getInPort());
        parseLiteral.setOutPort(stmtEnd.getSubInPort());
        stmtEnd.setSubOutPort(2, spaceComment.getInPort());
        spaceComment.setOutPort(stmtEnd.getSubInPort());
    }

    private void initConfig() {
        ParseLiteral.ParseLiteralConfig parseRegexConfig = new ParseLiteral.ParseLiteralConfig(";");
        parseLiteral.getConfigPort().send(parseRegexConfig);
    }

    public Port<T> getInPort() {
        return stmtEnd.getInPort();
    }

    public void setOutPort(Port<T> port) {
        stmtEnd.setOutPort(port);
    }
}
