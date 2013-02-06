package org.flowdev.flowparser.rawdata;


public enum RawPortType {
	IN("in"),
	OUT("out");

	private final String name;

	RawPortType(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}

