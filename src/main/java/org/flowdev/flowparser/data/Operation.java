package org.flowdev.flowparser.data;

import java.util.List;


public class Operation {
	public String operationName;
	public String operationType;
	public String operationGenerics;
	public boolean hasParams;
	public List<GetterData> getters;
	public List<Setter> setters;
	public List<Creator> creators;
}
