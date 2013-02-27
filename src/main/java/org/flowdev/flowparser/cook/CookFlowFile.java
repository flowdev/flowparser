package org.flowdev.flowparser.cook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.data.GetterData;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.Port;
import org.flowdev.flowparser.data.Version;
import org.flowdev.flowparser.rawdata.RawConnectionChain;
import org.flowdev.flowparser.rawdata.RawConnectionPart;
import org.flowdev.flowparser.rawdata.RawFlow;
import org.flowdev.flowparser.rawdata.RawFlowFile;
import org.flowdev.flowparser.rawdata.RawGetter;
import org.flowdev.flowparser.rawdata.RawOperation;
import org.flowdev.flowparser.rawdata.RawPort;
import org.flowdev.flowparser.rawdata.RawPortType;
import org.flowdev.flowparser.rawdata.RawVersion;

/**
 * This operation reads the content of a file as a UTF-8 text into a string.
 */
public class CookFlowFile<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
	public Getter<T, String> getFileName;
	public Getter<T, RawFlowFile> getRawFlowFile;
	public Setter<T, FlowFile> setCookedFlowFile;
    }

    public static final String IMPORT_PORT = "org.flowdev.Port";
    public static final String IMPORT_GETTER = "org.flowdev.Getter";
    public static final String IMPORT_SETTER = "org.flowdev.Setter";

    private final Params<T> params;
    private final String sourceRoot = System.getProperty("source.base", ".");
    private final String flowRoot = sourceRoot + "/flow";
    private String fileName;
    private boolean hasGetter;
    private boolean hasSetter;

    public CookFlowFile(Params<T> params) {
	this.params = params;
    }

    protected T filter(T data) {
	fileName = params.getFileName.get(data);
	RawFlowFile rawFlowFile = params.getRawFlowFile.get(data);
	FlowFile cookedFlowFile = new FlowFile();

	cookedFlowFile.version = cookVersion(rawFlowFile.version);
	cookedFlowFile.flows = cookFlows(rawFlowFile.flows);

	params.setCookedFlowFile.set(data, cookedFlowFile);
	return data;
    }

    private List<Flow> cookFlows(List<RawFlow> rawFlows) {
	List<Flow> flows = new ArrayList<>(rawFlows.size());
	for (RawFlow rflow : rawFlows) {
	    flows.add(cookFlow(rflow));
	}
	return flows;
    }

    private Flow cookFlow(RawFlow rflow) {
	hasGetter = false;
	hasSetter = false;
	Flow flow = new Flow();
	flow.imports = new ArrayList<>();
	flow.imports.add(IMPORT_PORT);
	flow.name = rflow.name;
	flow.module = file2package(fileName);
	cookPorts(rflow, flow);
	cookConnections(rflow, flow);
	cookOperations(rflow, flow);
	return flow;
    }

    private void cookPorts(RawFlow rflow, Flow flow) {
	flow.inPorts = new ArrayList<>();
	flow.outPorts = new ArrayList<>();

	for (RawPort rport : rflow.ports) {
	    if (rport.type == RawPortType.IN) {
		cookInPort(rport, rflow, flow);
	    } else {
		cookOutPort(rport, rflow, flow);
	    }
	}
    }

    private void cookInPort(RawPort rport, RawFlow rflow, Flow flow) {
	Port port = new Port();
	port.name = rport.name;
	port.type = rport.dataType.type;

	RawConnectionPart connPart = findInConnectionPart(port.name, rflow);
	port.operationName = connPart.operationName;
	port.oparationPort = connPart.inPort.name;

	flow.inPorts.add(port);
    }

    private RawConnectionPart findInConnectionPart(String portName,
	    RawFlow rflow) {
	for (RawConnectionChain connChain : rflow.connections) {
	    RawConnectionPart connPart = connChain.parts.get(0);
	    if (isParentPart(connPart) && portName.equals(connPart.inPort.name)) {
		return connChain.parts.get(1);
	    }
	}
	throw new RuntimeException("Unable to find input port '" + portName
		+ "' in the connections of the raw flow: "
		+ PrettyPrinter.prettyPrint(rflow));
    }

    private void cookOutPort(RawPort rport, RawFlow rflow, Flow flow) {
	Port port = new Port();
	port.name = rport.name;
	port.type = rport.dataType.type;

	RawConnectionPart connPart = findOutConnectionPart(port.name, rflow);
	port.operationName = connPart.operationName;
	port.oparationPort = connPart.outPort.name;

	flow.outPorts.add(port);
    }

    private RawConnectionPart findOutConnectionPart(String portName,
	    RawFlow rflow) {
	for (RawConnectionChain connChain : rflow.connections) {
	    int last = connChain.parts.size() - 1;
	    RawConnectionPart connPart = connChain.parts.get(last);
	    if (isParentPart(connPart)
		    && portName.equals(connPart.outPort.name)) {
		return connChain.parts.get(last - 1);
	    }
	}
	throw new RuntimeException("Unable to find output port '" + portName
		+ "in raw flow: " + PrettyPrinter.prettyPrint(rflow));
    }

    private void cookConnections(RawFlow rflow, Flow flow) {
	flow.connections = new ArrayList<>();
	for (RawConnectionChain connChain : rflow.connections) {
	    int last = connChain.parts.size() - 1;
	    for (int i = 0; i < last; i++) {
		addConnection(connChain.parts.get(i),
			connChain.parts.get(i + 1), flow.connections);
	    }
	}
    }

    private void addConnection(RawConnectionPart from, RawConnectionPart to,
	    List<Connection> conns) {
	if (!isParentPart(from) && !isParentPart(to)) {
	    Connection conn = new Connection();
	    conn.fromOperation = from.operationName;
	    conn.fromPort = from.outPort.name;
	    conn.toOperation = to.operationName;
	    conn.toPort = to.inPort.name;

	    conns.add(conn);
	}
    }

    private void cookOperations(RawFlow rflow, Flow flow) {
	flow.operations = new ArrayList<>();
	for (RawOperation rawOp : rflow.operations) {
	    flow.operations.add(cookOperation(rawOp));
	}
    }

    private Operation cookOperation(RawOperation rawOp) {
	Operation op = new Operation();
	op.operationName = rawOp.name;
	op.operationType = rawOp.type.type;
	op.hasParams = cookOpHasParams(rawOp);
	op.getters = cookOpGetters(rawOp.getters);
	// op.setters = cookOpSetters(rawOp.setters);
	// op.creators = cookOpCreators(rawOp.creators);

	return op;
    }

    private boolean cookOpHasParams(RawOperation op) {
	return (op.getters != null && !op.getters.isEmpty())
		|| (op.setters != null && !op.setters.isEmpty())
		|| (op.creators != null && !op.creators.isEmpty());
    }

    private List<GetterData> cookOpGetters(List<RawGetter> rawGetters) {
	if (rawGetters == null) {
	    return Collections.emptyList();
	}

	List<GetterData> getters = new ArrayList<>(rawGetters.size());
	for (RawGetter rawGetter : rawGetters) {
	    getters.add(cookOpGetter(rawGetter));
	}
	return getters;
    }

    private GetterData cookOpGetter(RawGetter rawGetter) {
	GetterData getter = new GetterData();
	getter.name = rawGetter.name;
	return getter;
    }

    private Version cookVersion(RawVersion rawVers) {
	Version vers = new Version();
	vers.political = rawVers.political;
	vers.major = rawVers.major;
	return vers;
    }

    private boolean isParentPart(RawConnectionPart part) {
	return part.operationName == null;
    }

    private String file2package(String module) {
	// TODO: implement!
	return module;
    }

    private String capFirst(String s) {
	return "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
