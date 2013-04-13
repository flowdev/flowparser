package org.flowdev.flowparser.mustache;

import java.util.Map;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.op.Configure;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.FlowFile;

public class OutputFlowFile<T> extends Configure<OutputFlowFileConfig> {
    public static class Params<T> {
	public Getter<T, FlowFile> getFlowFile;
    }

    private final Params<T> params;
    private Port<OutFileData> out;
    private final Port<T> in = new Port<T>() {
	@Override
	public void send(T data) {
	    outputFlowFile(data);
	}
    };

    public OutputFlowFile(Params<T> params) {
	this.params = params;
    }

    private void outputFlowFile(T data) {
	FlowFile flowFile = params.getFlowFile.get(data);
	Map<String, String> roots = getVolatileConfig().roots;
	System.out.println("Roots: " + roots.toString());
	System.out.println("Flows: " + flowFile.toString());

	for (Map.Entry<String, String> entry : roots.entrySet()) {
	    outputFlowFileFormatted(entry.getKey(), entry.getValue(), flowFile);
	}
    }

    private void outputFlowFileFormatted(String format, String root,
	    FlowFile flowFile) {
	for (Flow flow : flowFile.flows) {
	    outputFlowFormatted(format, root, flow);
	}
    }

    private void outputFlowFormatted(String format, String root, Flow flow) {

    }

    public Port<T> getIn() {
	return in;
    }

    public void setOut(Port<OutFileData> out) {
	this.out = out;
    }
}
