package org.flowdev.flowparser.data;

import java.util.List;


public class Flow {
	public String module;
	public String name;
	public List<String> imports;
	public List<Port> inPorts;
	public List<Port> outPorts;
	public List<Connection> connections;
	public List<Operation> operations;
}
