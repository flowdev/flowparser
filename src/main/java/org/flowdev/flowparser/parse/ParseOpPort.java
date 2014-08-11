package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseOptional;
import org.flowdev.parser.op.ParserParams;

public class ParseOpPort<T> implements Filter<T, NoConfig> {
    private ParseOptional<T> opPort;
    private ParsePort<T> port;

    public ParseOpPort(ParserParams<T> params) {
        opPort = new ParseOptional<>(params);
        port = new ParsePort<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        opPort.setSubOutPort(port.getInPort());
        port.setOutPort(opPort.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return opPort.getInPort();
    }

    public void setOutPort(Port<T> outPort) {
        opPort.setOutPort(outPort);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> errorPort) {
        port.setErrorPort(errorPort);
        opPort.setErrorPort(errorPort);
    }
}
