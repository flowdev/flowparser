package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.parser.op.ParseRegex;

import static org.flowdev.parser.op.BaseParser.Params;


@SuppressWarnings("WeakerAccess")
public class BigIdent<T> {
    private final ParseRegex<T> parseRegex;

    public BigIdent(Params<T> params) {
        parseRegex = new ParseRegex<>(params);

        initConfig();
    }

    private void initConfig() {
        ParseRegex.ParseRegexConfig parseRegexConfig = new ParseRegex.ParseRegexConfig("[A-Z][a-zA-Z0-9]+");
        parseRegex.getConfigPort().send(parseRegexConfig);
    }

    public Port<T> getInPort() {
        return parseRegex.getInPort();
    }

    public void setOutPort(Port<T> port) {
        parseRegex.setOutPort(port);
    }
}
