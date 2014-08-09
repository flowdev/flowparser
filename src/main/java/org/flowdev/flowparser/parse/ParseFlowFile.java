package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseEof;
import org.flowdev.parser.op.ParserParams;

public class ParseFlowFile<T> implements Filter<T, NoConfig> {
    private ParseAll<T> flowFile;
    private SemanticCreateFlowFile<T> semantic;
    private ParseVersion<T> version;
    private ParseEof<T> eof;

    public ParseFlowFile(ParserParams<T> params) {
        flowFile = new ParseAll<>(params);
        semantic = new SemanticCreateFlowFile<T>(params);
        version = new ParseVersion<>(params);
        eof = new ParseEof<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        flowFile.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(flowFile.getSemInPort());
        flowFile.setSubOutPort(0, version.getInPort());
        version.setOutPort(flowFile.getSubInPort());
        flowFile.setSubOutPort(1, eof.getInPort());
        eof.setOutPort(flowFile.getSubInPort());
    }

    private void initConfig() {
    }

    public Port<T> getInPort() {
        return flowFile.getInPort();
    }

    public void setOutPort(Port<T> port) {
        flowFile.setOutPort(port);
    }

    @Override
    public Port<NoConfig> getConfigPort() {
        return null;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        version.setErrorPort(port);
        semantic.setErrorPort(port);
        eof.setErrorPort(port);
    }
}
