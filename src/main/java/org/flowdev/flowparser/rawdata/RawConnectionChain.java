package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawConnectionChain extends RawNode {
    public RawPort inPort;
	public List<RawConnectionPart> parts;
    public RawPort outPort;
}
