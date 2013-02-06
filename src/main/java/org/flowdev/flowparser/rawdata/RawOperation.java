package org.flowdev.flowparser.rawdata;

import java.util.List;


public class RawOperation extends RawNode {
	public String name;
	public RawDataType type;
	public List<RawGetter> getters;
	public List<RawSetter> setters;
	public List<RawCreator> creators;
	public List<RawConfig> configs;
}
