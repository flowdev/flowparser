package org.flowdev.flowparser;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.op.io.ReadTextFile;
import org.flowdev.flowparser.cook.CookFlowFile;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.mustache.OutputFlowFile;
import org.flowdev.flowparser.mustache.OutputFlowFileConfig;
import org.flowdev.flowparser.rawdata.RawFlowFile;

/**
 * This operation parses a flow file from a string and an optional file name to
 * a raw flow file object.
 */
public class MainFlow implements IMainFlow {
    private final ReadTextFile<MainData> readTextFile;
    private final ParseToRawFlowFile<MainData> parseToRawFlowFile;
    private final CookFlowFile<MainData> cookFlowFile;
    private final OutputFlowFile<MainData> outputFlowFile;
    private final Port<MainConfig> config = new Port<MainConfig>() {
	@Override
	public void send(MainConfig data) {
	    if (data.outputFlowFile != null) {
		outputFlowFile.getConfig().send(data.outputFlowFile);
	    }
	}
    };

    public MainFlow() {
	ReadTextFile.Params<MainData> readTextFileParams = new ReadTextFile.Params<>();
	readTextFileParams.getFileName = new Getter<MainData, String>() {
	    @Override
	    public String get(MainData data) {
		return data.fileName;
	    }
	};
	readTextFileParams.setFileContent = new Setter<MainData, String>() {
	    @Override
	    public void set(MainData data, String subdata) {
		data.fileContent = subdata;
	    }
	};
	readTextFile = new ReadTextFile<>(readTextFileParams);

	ParseToRawFlowFile.Params<MainData> parseToRawFlowFileParams = new ParseToRawFlowFile.Params<>();
	parseToRawFlowFileParams.getFileName = new Getter<MainData, String>() {
	    @Override
	    public String get(MainData data) {
		return data.fileName;
	    }
	};
	parseToRawFlowFileParams.getFileContent = new Getter<MainData, String>() {
	    @Override
	    public String get(MainData data) {
		return data.fileContent;
	    }
	};
	parseToRawFlowFileParams.setFlowFile = new Setter<MainData, RawFlowFile>() {
	    @Override
	    public void set(MainData data, RawFlowFile subdata) {
		data.rawFlowFile = subdata;
	    }
	};
	parseToRawFlowFile = new ParseToRawFlowFile<>(parseToRawFlowFileParams);

	CookFlowFile.Params<MainData> cookFlowFileParams = new CookFlowFile.Params<>();
	cookFlowFileParams.getFileName = new Getter<MainData, String>() {
	    @Override
	    public String get(MainData data) {
		return data.fileName;
	    }
	};
	cookFlowFileParams.getRawFlowFile = new Getter<MainData, RawFlowFile>() {
	    @Override
	    public RawFlowFile get(MainData data) {
		return data.rawFlowFile;
	    }
	};
	cookFlowFileParams.setCookedFlowFile = new Setter<MainData, FlowFile>() {
	    @Override
	    public void set(MainData data, FlowFile subdata) {
		data.flowFile = subdata;
	    }
	};
	cookFlowFile = new CookFlowFile<>(cookFlowFileParams);

	OutputFlowFile.Params<MainData> outputFlowFileParams = new OutputFlowFile.Params<>();
	outputFlowFileParams.getFlowFile = new Getter<MainData, FlowFile>() {
	    @Override
	    public FlowFile get(MainData data) {
		return data.flowFile;
	    }
	};
	outputFlowFile = new OutputFlowFile<>(outputFlowFileParams);

	createConnections();
	initConfig();
    }

    public Port<MainData> getIn() {
	return readTextFile.getIn();
    }

    public Port<MainConfig> getConfig() {
	return config;
    }

    private void createConnections() {
	readTextFile.setOut(parseToRawFlowFile.getIn());
	parseToRawFlowFile.setOut(cookFlowFile.getIn());
	cookFlowFile.setOut(outputFlowFile.getIn());
    }

    private void initConfig() {
	OutputFlowFileConfig outputFlowFileConfig = new OutputFlowFileConfig();
	outputFlowFileConfig.roots.put("json", ".");
	outputFlowFileConfig.roots.put("java", ".");
	outputFlowFile.getConfig().send(outputFlowFileConfig);
    }
}
