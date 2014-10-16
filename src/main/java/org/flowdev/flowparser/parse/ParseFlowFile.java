package org.flowdev.flowparser.parse;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.semantic.SemanticFlowFile;
import org.flowdev.parser.data.UseTextSemanticConfig;
import org.flowdev.parser.op.ParseAll;
import org.flowdev.parser.op.ParseEof;
import org.flowdev.parser.op.ParseMultiple1;
import org.flowdev.parser.op.ParserParams;

public class ParseFlowFile<T> implements Filter<T, NoConfig> {
    private ParseAll<T> flowFile;
    private SemanticFlowFile<T> semantic;
    private ParseVersion<T> version;
    private ParseMultiple1<T> flows;
    private ParseFlow<T> flow;
    private ParseEof<T> eof;

    public ParseFlowFile(ParserParams<T> params) {
        flowFile = new ParseAll<>(params);
        semantic = new SemanticFlowFile<>(params);
        version = new ParseVersion<>(params);
        flows = new ParseMultiple1<>(params);
        flow = new ParseFlow<>(params);
        eof = new ParseEof<>(params);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        flowFile.setSemOutPort(semantic.getInPort());
        semantic.setOutPort(flowFile.getSemInPort());
        flowFile.setSubOutPort(0, version.getInPort());
        version.setOutPort(flowFile.getSubInPort());
        flowFile.setSubOutPort(1, flows.getInPort());
        flows.setOutPort(flowFile.getSubInPort());
        flowFile.setSubOutPort(2, eof.getInPort());
        eof.setOutPort(flowFile.getSubInPort());

        flows.setSubOutPort(flow.getInPort());
        flow.setOutPort(flows.getSubInPort());
    }

    private void initConfig() {
        flowFile.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(false));
        flows.getConfigPort().send(new UseTextSemanticConfig().useTextSemantic(false));
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
        flowFile.setErrorPort(port);
        semantic.setErrorPort(port);
        version.setErrorPort(port);
        flows.setErrorPort(port);
        eof.setErrorPort(port);
    }
}
