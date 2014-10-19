package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.op.ParseRegex;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseRegex.ParseRegexConfig;


public class ParseSmallIdent implements Filter<MainData, NoConfig> {
    private final ParseRegex<MainData> smallIdent;

    public ParseSmallIdent(ParserParams<MainData> params) {
        smallIdent = new ParseRegex<>(params);

        initConfig();
    }

    private void initConfig() {
        smallIdent.getConfigPort().send(new ParseRegexConfig().regex("[a-z][a-zA-Z0-9]*"));
    }

    public Port<MainData> getInPort() {
        return smallIdent.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
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
