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
    private final ParseToRawFlowFile parseToRawFlowFile;
    private final CookFlowFile cookFlowFile;
    private final OutputAllFormats outputAllFormats;
    private final FillTemplate fillTemplate;
    private final CreateOutputFileName createOutputFileName;
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

        parseToRawFlowFile = new ParseToRawFlowFile();

        cookFlowFile = new CookFlowFile();

        outputAllFormats = new OutputAllFormats();

        fillTemplate = new FillTemplate();

        createOutputFileName = new CreateOutputFileName();

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
