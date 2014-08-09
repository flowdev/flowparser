package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.data.UseTextSemanticConfig;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseLiteral;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseLiteral.ParseLiteralConfig;

public class ParseStatementEnd<T> implements Filter<T, NoConfig> {
    private ParseAll<T> stmtEnd;
    private ParseSpaceComment<T> spaceCommentBeg;
    private ParseLiteral<T> semicolon;
    private ParseSpaceComment<T> spaceCommentEnd;

    public ParseStatementEnd(ParserParams<T> params) {
        stmtEnd = new ParseAll<>(params);
        spaceCommentBeg = new ParseSpaceComment<>(params);
        semicolon = new ParseLiteral<>(params);
        spaceCommentEnd = new ParseSpaceComment<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        stmtEnd.setSubOutPort(0, spaceCommentBeg.getInPort());
        spaceCommentBeg.setOutPort(stmtEnd.getSubInPort());
        stmtEnd.setSubOutPort(1, semicolon.getInPort());
        semicolon.setOutPort(stmtEnd.getSubInPort());
        stmtEnd.setSubOutPort(2, spaceCommentEnd.getInPort());
        spaceCommentEnd.setOutPort(stmtEnd.getSubInPort());
    }

    private void initConfig() {
        stmtEnd.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(true));
        semicolon.getConfigPort().send(new ParseLiteralConfig().literal(";"));
    }

    public Port<T> getInPort() {
        return stmtEnd.getInPort();
    }

    public void setOutPort(Port<T> port) {
        stmtEnd.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        stmtEnd.setErrorPort(port);
        spaceCommentBeg.setErrorPort(port);
        semicolon.setErrorPort(port);
        spaceCommentEnd.setErrorPort(port);
    }
}
