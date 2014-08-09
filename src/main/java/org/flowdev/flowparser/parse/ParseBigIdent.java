package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseRegex;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseRegex.ParseRegexConfig;


public class ParseBigIdent<T> implements Filter<T, NoConfig> {
    private final ParseRegex<T> bigIdent;

    public ParseBigIdent(ParserParams<T> params) {
        bigIdent = new ParseRegex<>(params);

        initConfig();
    }

    private void initConfig() {
        bigIdent.getConfigPort().send(new ParseRegexConfig().regex("[A-Z][a-zA-Z0-9]+"));
    }

    public Port<T> getInPort() {
        return bigIdent.getInPort();
    }

    public void setOutPort(Port<T> port) {
        bigIdent.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        bigIdent.setErrorPort(port);
    }
}
