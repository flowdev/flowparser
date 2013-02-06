package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawFlow extends RawNode {
	public String name;
	public List<RawPort> ports;
	public List<RawConnectionChain> connections;
	public List<RawOperation> operations;
}
