package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.parser.data.ParseSpaceConfig;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParseSpace;

import static org.flowdev.parser.op.BaseParser.Params;

public class OpSpc<T> {
    private ParseSpace<T> parseSpace;
    private ParseOptional<T> opSpc;

    public OpSpc(Params<T> params) {
        parseSpace = new ParseSpace<>(params);
        opSpc = new ParseOptional<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        opSpc.setSubOutPort(parseSpace.getInPort());
        parseSpace.setOutPort(opSpc.getSubInPort());
    }

    private void initConfig() {
        ParseSpaceConfig parseSpaceConfig = new ParseSpaceConfig(false);
        parseSpace.getConfigPort().send(parseSpaceConfig);
    }

    public Port<T> getInPort() {
        return opSpc.getInPort();
    }

    public void setOutPort(Port<T> port) {
        opSpc.setOutPort(port);
    }
}
