package org.flowdev.flowparser;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.base.op.io.ReadTextFile;
import org.flowdev.flowparser.cook.CookFlowFile;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.rawdata.RawFlowFile;

public class TstParser {
    public static class TstData {
	public String fileName;
	public String fileContent;
	public RawFlowFile rawFlowFile;
	public FlowFile flowFile;
    }

    private ReadTextFile<TstData, TstData> readFlowFile;
    private ParseToRawFlowFile<TstData> parseToRawFlowFile;
    private CookFlowFile<TstData> cookFlowFile;

    public TstParser() {
	ReadTextFile.Params<TstData, TstData> readFlowFileParams = new ReadTextFile.Params<>();
	readFlowFileParams.getFileName = new Getter<TstData, String>() {
	    @Override
	    public String get(TstData data) {
		return data.fileName;
	    }
	};
	readFlowFileParams.setFileContent = new Setter<String, TstData, TstData>() {
	    @Override
	    public TstData set(TstData data, String subdata) {
		data.fileContent = subdata;
		return data;
	    }
	};
	readFlowFile = new ReadTextFile<>(readFlowFileParams);

	ParseToRawFlowFile.Params<TstData> parseToRawFlowFileParams = new ParseToRawFlowFile.Params<>();
	parseToRawFlowFileParams.getFileName = new Getter<TstData, String>() {
	    @Override
	    public String get(TstData data) {
		return data.fileName;
	    }
	};
	parseToRawFlowFileParams.getFileContent = new Getter<TstData, String>() {
	    @Override
	    public String get(TstData data) {
		return data.fileContent;
	    }
	};
	parseToRawFlowFileParams.setFlowFile = new Setter<RawFlowFile, TstData, TstData>() {
	    @Override
	    public TstData set(TstData data, RawFlowFile subdata) {
		data.rawFlowFile = subdata;
		return data;
	    }
	};
	parseToRawFlowFile = new ParseToRawFlowFile<>(parseToRawFlowFileParams);

	CookFlowFile.Params<TstData> cookFlowFileParams = new CookFlowFile.Params<>();
	cookFlowFileParams.getFileName = new Getter<TstData, String>() {
	    @Override
	    public String get(TstData data) {
		return data.fileName;
	    }
	};
	cookFlowFileParams.getRawFlowFile = new Getter<TstData, RawFlowFile>() {
	    @Override
	    public RawFlowFile get(TstData data) {
		return data.rawFlowFile;
	    }
	};
	cookFlowFileParams.setCookedFlowFile = new Setter<FlowFile, TstData, TstData>() {
	    @Override
	    public TstData set(TstData data, FlowFile subdata) {
		data.flowFile = subdata;
		return data;
	    }
	};
	cookFlowFile = new CookFlowFile<>(cookFlowFileParams);

	initConnections();
    }

    private void initConnections() {
	readFlowFile.setOutPort(parseToRawFlowFile.getInPort());
	parseToRawFlowFile.setOutPort(cookFlowFile.getInPort());
    }

    public Port<TstData> getIn() {
	return readFlowFile.getInPort();
    }

    public void setOut(Port<TstData> out) {
	cookFlowFile.setOutPort(out);
    }

    public static void main(String[] args) throws Exception {
	TstData data = new TstData();
	data.fileName = args[0];

	TstParser tstParser = new TstParser();
	tstParser.setOut(new Port<TstData>() {
	    @Override
	    public void send(TstData data) {
		System.out.println(PrettyPrinter.prettyPrint(data.flowFile));
	    }
	});
	tstParser.getIn().send(data);
    }
}
