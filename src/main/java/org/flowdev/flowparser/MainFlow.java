package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.op.io.ReadTextFileJava6;
import org.flowdev.base.op.io.WriteTextFileJava6;
import org.flowdev.flowparser.cook.CookFlowFile;
import org.flowdev.flowparser.output.CreateOutputFileName;
import org.flowdev.flowparser.output.FillTemplate;
import org.flowdev.flowparser.output.OutputAllFormats;
import org.flowdev.flowparser.output.OutputAllFormatsConfig;

/**
 * This flow parses a flow file and creates one ore more output files.
 */
public class MainFlow implements IMainFlow {
    private final ReadTextFileJava6<MainData, MainData> readTextFile;
    private final ParseToRawFlowFile parseToRawFlowFile;
    private final CookFlowFile cookFlowFile;
    private final OutputAllFormats outputAllFormats;
    private final FillTemplate fillTemplate;
    private final CreateOutputFileName<MainData> createOutputFileName;
    private final WriteTextFileJava6<MainData> writeTextFile;
    // Getting a compiler error if replacing the anonymous inner class with a lambda expression!
    private final Port<MainConfig> configPort = new Port<MainConfig>() {
        @Override
        public void send(MainConfig data) {
            outputAllFormats.getConfigPort().send(data.outputAllFormats);
        }
    };

    public MainFlow() {
        ReadTextFileJava6.Params<MainData, MainData> readTextFileParams = new ReadTextFileJava6.Params<>();
        readTextFileParams.getFileName = data -> data.fileName;
        readTextFileParams.setFileContent = (data, subdata) -> {
            data.fileContent = subdata;
            return data;
        };
        readTextFile = new ReadTextFileJava6<>(readTextFileParams);

        parseToRawFlowFile = new ParseToRawFlowFile();

        cookFlowFile = new CookFlowFile();

        outputAllFormats = new OutputAllFormats();

        fillTemplate = new FillTemplate();

        CreateOutputFileName.Params<MainData> createOutputFileNameParams = new CreateOutputFileName.Params<>();
        createOutputFileNameParams.getFileName = data -> data.flowFile.fileName;
        createOutputFileNameParams.getFormat = data -> data.format;
        createOutputFileNameParams.setFileName = (data, subdata) -> {
            data.fileName = subdata;
            return data;
        };
        createOutputFileName = new CreateOutputFileName<>(createOutputFileNameParams);

        WriteTextFileJava6.Params<MainData> writeTextFileParams = new WriteTextFileJava6.Params<>();
        writeTextFileParams.getFileContent = data -> data.fileContent;
        writeTextFileParams.getFileName = data -> data.fileName;
        writeTextFile = new WriteTextFileJava6<>(writeTextFileParams);

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
