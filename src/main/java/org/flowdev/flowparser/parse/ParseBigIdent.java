package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.op.ParseRegex;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseRegex.ParseRegexConfig;


public class ParseBigIdent implements Filter<MainData, NoConfig> {
    private final ParseRegex<MainData> bigIdent;

    public ParseBigIdent(ParserParams<MainData> params) {
        bigIdent = new ParseRegex<>(params);

        initConfig();
    }

    private void initConfig() {
        bigIdent.getConfigPort().send(new ParseRegexConfig().regex("[A-Z][a-zA-Z0-9]+"));
    }

    public Port<MainData> getInPort() {
        return bigIdent.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
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
