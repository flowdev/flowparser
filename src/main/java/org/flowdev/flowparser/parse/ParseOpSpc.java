package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.data.UseTextSemanticConfig;
import org.flowdev.parser.op.ParseOptionalSync;
import org.flowdev.parser.op.ParseSpace;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseSpace.ParseSpaceConfig;

public class ParseOpSpc<T> implements Filter<T, NoConfig> {
    private ParseOptionalSync<T> opSpc;
    private ParseSpace<T> space;

    public ParseOpSpc(ParserParams<T> params) {
        opSpc = new ParseOptionalSync<>(params);
        space = new ParseSpace<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        opSpc.setSubOutPort(space.getInPort());
        space.setOutPort(opSpc.getSubInPort());
    }

    private void initConfig() {
        opSpc.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(true));
        space.getConfigPort().send(new ParseSpaceConfig().acceptNewline(false));
    }

    public Port<T> getInPort() {
        return opSpc.getInPort();
    }

    public void setOutPort(Port<T> port) {
        opSpc.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        opSpc.setErrorPort(port);
        space.setErrorPort(port);
    }
}
