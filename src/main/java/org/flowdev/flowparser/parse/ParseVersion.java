package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.*;

import static org.flowdev.parser.op.ParseLiteral.ParseLiteralConfig;
import static org.flowdev.parser.op.ParseSpace.ParseSpaceConfig;


public class ParseVersion<T> implements Filter<T, NoConfig> {
    private ParseAllSync<T> version;
    private SemanticCreateVersion<T> semanticCreateVersion;
    private ParseSpaceComment<T> spcCommBeg;
    private ParseLiteral<T> vers;
    private ParseSpace<T> aspc;
    private ParseNatural<T> political;
    private ParseLiteral<T> dot;
    private ParseNatural<T> major;
    private ParseSpaceComment<T> spcCommEnd;

    public ParseVersion(ParserParams<T> params) {
        version = new ParseAllSync<>(params);
        semanticCreateVersion = new SemanticCreateVersion<>(params);
        spcCommBeg = new ParseSpaceComment<>(params);
        vers = new ParseLiteral<>(params);
        aspc = new ParseSpace<>(params);
        political = new ParseNatural<>(params);
        dot = new ParseLiteral<>(params);
        major = new ParseNatural<>(params);
        spcCommEnd = new ParseSpaceComment<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        version.setSemOutPort(semanticCreateVersion.getInPort());
        semanticCreateVersion.setOutPort(version.getSemInPort());
        version.setSubOutPort(0, spcCommBeg.getInPort());
        spcCommBeg.setOutPort(version.getSubInPort());
        version.setSubOutPort(1, vers.getInPort());
        vers.setOutPort(version.getSubInPort());
        version.setSubOutPort(2, aspc.getInPort());
        aspc.setOutPort(version.getSubInPort());
        version.setSubOutPort(3, political.getInPort());
        political.setOutPort(version.getSubInPort());
        version.setSubOutPort(4, dot.getInPort());
        dot.setOutPort(version.getSubInPort());
        version.setSubOutPort(5, major.getInPort());
        major.setOutPort(version.getSubInPort());
        version.setSubOutPort(6, spcCommEnd.getInPort());
        spcCommEnd.setOutPort(version.getSubInPort());
    }

    private void initConfig() {
        vers.getConfigPort().send(new ParseLiteralConfig().literal("version"));
        aspc.getConfigPort().send(new ParseSpaceConfig().acceptNewline(false));
        dot.getConfigPort().send(new ParseLiteralConfig().literal("."));
    }

    public Port<T> getInPort() {
        return version.getInPort();
    }

    public void setOutPort(Port<T> port) {
        version.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        version.setErrorPort(port);
        semanticCreateVersion.setErrorPort(port);
        spcCommBeg.setErrorPort(port);
        vers.setErrorPort(port);
        aspc.setErrorPort(port);
        political.setErrorPort(port);
        dot.setErrorPort(port);
        major.setErrorPort(port);
        spcCommEnd.setErrorPort(port);
    }
}

