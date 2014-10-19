package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.UseTextSemanticConfig;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParseSpace;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.parser.op.ParseSpace.ParseSpaceConfig;

public class ParseOptSpc implements Filter<MainData, NoConfig> {
    private ParseOptional<MainData> optSpc;
    private ParseSpace<MainData> space;

    public ParseOptSpc(ParserParams<MainData> params) {
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

    public Port<MainData> getInPort() {
        return optSpc.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
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
