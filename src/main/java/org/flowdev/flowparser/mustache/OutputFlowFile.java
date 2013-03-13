package org.flowdev.flowparser.mustache;

import java.util.Map;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.op.Configure;
import org.flowdev.flowparser.data.FlowFile;

public class OutputFlowFile<T> extends Configure<OutputFlowFileConfig> {
    public static class Params<T> {
	public Getter<T, FlowFile> getFlowFile;
    }

    private final Params<T> params;
    private final Port<T> inPort = new Port<T>() {
	@Override
	public void send(T data) {
	    FlowFile flowFile = params.getFlowFile.get(data);
	    Map<String, String> roots = getVolatileConfig().roots;
	    System.out.println("Roots: " + roots.toString());
	    System.out.println("Flows: " + flowFile.toString());
	}
    };

    public OutputFlowFile(Params<T> params) {
	this.params = params;
    }

    public Port<T> getIn() {
	return inPort;
    }
}
