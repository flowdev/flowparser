package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.op.io.ReadTextFile;
import org.flowdev.base.op.io.WriteTextFile;
import org.flowdev.flowparser.cook.CookFlowFile;
import org.flowdev.flowparser.output.CreateOutputFileName;
import org.flowdev.flowparser.output.FillTemplate;
import org.flowdev.flowparser.output.OutputAllFormats;
import org.flowdev.flowparser.output.OutputAllFormatsConfig;

/**
 * This flow parses a flow file and creates one ore more output files.
 */
public class MainFlow implements IMainFlow {
    private final ReadTextFile<MainData, MainData> readTextFile;
    private final ParseToRawFlowFile<MainData> parseToRawFlowFile;
    private final CookFlowFile<MainData> cookFlowFile;
    private final OutputAllFormats<MainData> outputAllFormats;
    private final FillTemplate<MainData> fillTemplate;
    private final CreateOutputFileName<MainData> createOutputFileName;
    private final WriteTextFile<MainData> writeTextFile;
    // Getting a compiler error if replacing the anonymous inner class with a lambda expression!
    private final Port<MainConfig> configPort = new Port<MainConfig>() {
        @Override
        public void send(MainConfig data) {
            outputAllFormats.getConfigPort().send(data.outputAllFormats);
        }
    };

    public MainFlow() {
        ReadTextFile.Params<MainData, MainData> readTextFileParams = new ReadTextFile.Params<>();
        readTextFileParams.getFileName = data -> data.fileName;
        readTextFileParams.setFileContent = (data, subdata) -> {
            data.fileContent = subdata;
            return data;
        };
        readTextFile = new ReadTextFile<>(readTextFileParams);

        ParseToRawFlowFile.Params<MainData> parseToRawFlowFileParams = new ParseToRawFlowFile.Params<>();
        parseToRawFlowFileParams.getFileName = data -> data.fileName;
        parseToRawFlowFileParams.getFileContent = data -> data.fileContent;
        parseToRawFlowFileParams.setFlowFile = (data, subdata) -> {
            data.rawFlowFile = subdata;
            return data;
        };
        parseToRawFlowFile = new ParseToRawFlowFile<>(parseToRawFlowFileParams);

        CookFlowFile.Params<MainData> cookFlowFileParams = new CookFlowFile.Params<>();
        cookFlowFileParams.getFileName = data -> data.fileName;
        cookFlowFileParams.getRawFlowFile = data -> data.rawFlowFile;
        cookFlowFileParams.setCookedFlowFile = (data, subdata) -> {
            data.flowFile = subdata;
            return data;
        };
        cookFlowFile = new CookFlowFile<>(cookFlowFileParams);

        OutputAllFormats.Params<MainData> outputAllFormatsParams = new OutputAllFormats.Params<>();
        outputAllFormatsParams.setFormat = (data, subdata) -> {
            data.format = subdata;
            return data;
        };
        outputAllFormats = new OutputAllFormats<>(outputAllFormatsParams);

        FillTemplate.Params<MainData> fillTemplateParams = new FillTemplate.Params<>();
        fillTemplateParams.getFormat = data -> data.format;
        fillTemplateParams.getFlowFile = data -> data.flowFile;
        fillTemplateParams.setFileContent = (data, subdata) -> {
            data.fileContent = subdata;
            return data;
        };
        fillTemplate = new FillTemplate<>(fillTemplateParams);

        CreateOutputFileName.Params<MainData> createOutputFileNameParams = new CreateOutputFileName.Params<>();
        createOutputFileNameParams.getFileName = data -> data.flowFile.fileName;
        createOutputFileNameParams.getFormat = data -> data.format;
        createOutputFileNameParams.setFileName = (data, subdata) -> {
            data.fileName = subdata;
            return data;
        };
        createOutputFileName = new CreateOutputFileName<>(createOutputFileNameParams);

        WriteTextFile.Params<MainData> writeTextFileParams = new WriteTextFile.Params<>();
        writeTextFileParams.getFileContent = data -> data.fileContent;
        writeTextFileParams.getFileName = data -> data.fileName;
        writeTextFile = new WriteTextFile<>(writeTextFileParams);

        createConnections();
        initConfig();
    }

    public Port<MainData> getInPort() {
        return readTextFile.getInPort();
    }

    public Port<MainConfig> getConfigPort() {
        return configPort;
    }

    private void createConnections() {
        readTextFile.setOutPort(parseToRawFlowFile.getInPort());
        parseToRawFlowFile.setOutPort(cookFlowFile.getInPort());
        cookFlowFile.setOutPort(outputAllFormats.getInPort());
        outputAllFormats.setOutPort(fillTemplate.getInPort());
        fillTemplate.setOutPort(createOutputFileName.getInPort());
        createOutputFileName.setOutPort(writeTextFile.getInPort());
    }

    private void initConfig() {
        OutputAllFormatsConfig outputAllFormatsConfig = new OutputAllFormatsConfig();
        outputAllFormatsConfig.formats.add("graphviz");
        outputAllFormatsConfig.formats.add("java6");
        outputAllFormats.getConfigPort().send(outputAllFormatsConfig);
    }
}
