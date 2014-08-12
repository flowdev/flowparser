package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.data.UseTextSemanticConfig;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParseSpace;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseSpace.ParseSpaceConfig;

public class ParseOptSpc<T> implements Filter<T, NoConfig> {
    private ParseOptional<T> optSpc;
    private ParseSpace<T> space;

    public ParseOptSpc(ParserParams<T> params) {
        optSpc = new ParseOptional<>(params);
        space = new ParseSpace<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        optSpc.setSubOutPort(space.getInPort());
        space.setOutPort(optSpc.getSubInPort());
    }

    private void initConfig() {
        optSpc.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(true));
        space.getConfigPort().send(new ParseSpaceConfig().acceptNewline(false));
    }

    public Port<T> getInPort() {
        return optSpc.getInPort();
    }

    public void setOutPort(Port<T> port) {
        optSpc.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        optSpc.setErrorPort(port);
        space.setErrorPort(port);
    }
}
