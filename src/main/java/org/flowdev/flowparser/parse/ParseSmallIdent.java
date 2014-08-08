package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseRegex;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseRegex.ParseRegexConfig;


public class ParseSmallIdent<T> implements Filter<T, NoConfig> {
    private final ParseRegex<T> smallIdent;

    public ParseSmallIdent(ParserParams<T> params) {
        smallIdent = new ParseRegex<>(params);

        initConfig();
    }

    private void initConfig() {
        smallIdent.getConfigPort().send(new ParseRegexConfig("[a-z][a-zA-Z0-9]*"));
    }

    public Port<T> getInPort() {
        return smallIdent.getInPort();
    }

    public void setOutPort(Port<T> port) {
        smallIdent.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        smallIdent.setErrorPort(port);
    }
}
