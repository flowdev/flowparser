package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;

public class ParseOptPort implements Filter<MainData, NoConfig> {
    private ParseOptional<MainData> optPort;
    private ParsePort port;

    public ParseOptPort(ParserParams<MainData> params) {
        optPort = new ParseOptional<>(params);
        port = new ParsePort(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        optPort.setSubOutPort(port.getInPort());
        port.setOutPort(optPort.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<MainData> getInPort() {
        return optPort.getInPort();
    }

    public void setOutPort(Port<MainData> outPort) {
        optPort.setOutPort(outPort);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> errorPort) {
        port.setErrorPort(errorPort);
        optPort.setErrorPort(errorPort);
    }
}
