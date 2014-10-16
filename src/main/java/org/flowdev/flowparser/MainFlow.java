package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.op.io.ReadTextFile;
import org.flowdev.base.op.io.WriteTextFile;
import org.flowdev.flowparser.output.*;
import org.flowdev.flowparser.parse.HandleParserResult;
import org.flowdev.flowparser.parse.ParseFlowFile;
import org.flowdev.parser.op.ParserParams;

/**
 * This flow parses a flow file and creates one ore more output files.
 */
public class MainFlow implements IMainFlow {
    private ReadTextFile<MainData, MainData> readTextFile;
    private ParseFlowFile<MainData> parseFlowFile;
    private HandleParserResult handleParserResult;
    private OutputAllFormats outputAllFormats;
    private FillPortPairs fillPortPairs;
    private FillTemplate fillTemplate;
    private CreateOutputFileName createOutputFileName;
    private WriteTextFile<MainData> writeTextFile;
    private Port<MainConfig> configPort = data -> outputAllFormats.getConfigPort().send(data.outputAllFormats);


    public MainFlow() {
        ReadTextFile.Params<MainData, MainData> readTextFileParams = new ReadTextFile.Params<>();
        readTextFileParams.getFileName = data -> data.parserData().source().name();
        readTextFileParams.setFileContent = (data, subdata) -> {
            data.parserData().source().content(subdata);
            return data;
        };
        readTextFile = new ReadTextFile<>(readTextFileParams);

        ParserParams<MainData> parserParams = new ParserParams<>();
        parserParams.getParserData = MainData::parserData;
        parserParams.setParserData = MainData::parserData;
        parseFlowFile = new ParseFlowFile<>(parserParams);

        handleParserResult = new HandleParserResult();

        outputAllFormats = new OutputAllFormats();

        fillPortPairs = new FillPortPairs();

        fillTemplate = new FillTemplate();

        createOutputFileName = new CreateOutputFileName();

        WriteTextFile.Params<MainData> writeTextFileParams = new WriteTextFile.Params<>();
        writeTextFileParams.getFileContent = MainData::outputContent;
        writeTextFileParams.getFileName = MainData::outputName;
        writeTextFile = new WriteTextFile<>(writeTextFileParams);

        createConnections();
        initConfig();
    }

    private void createConnections() {
        readTextFile.setOutPort(parseFlowFile.getInPort());
        parseFlowFile.setOutPort(handleParserResult.getInPort());
        handleParserResult.setOutPort(outputAllFormats.getInPort());
        outputAllFormats.setFormatPort(0, fillPortPairs.getInPort());
        outputAllFormats.setFormatPort(1, fillTemplate.getInPort());
        outputAllFormats.setFormatPort(2, fillPortPairs.getInPort());
        fillPortPairs.setOutPort(fillTemplate.getInPort());
        fillTemplate.setOutPort(createOutputFileName.getInPort());
        createOutputFileName.setOutPort(writeTextFile.getInPort());
    }

    private void initConfig() {
        OutputAllFormatsConfig outputAllFormatsConfig = new OutputAllFormatsConfig();
        outputAllFormatsConfig.formats.add("adoc");
        outputAllFormatsConfig.formats.add("wiki");
        outputAllFormatsConfig.formats.add("java");
        outputAllFormats.getConfigPort().send(outputAllFormatsConfig);
    }

    @Override
    public Port<MainData> getInPort() {
        return readTextFile.getInPort();
    }

    @Override
    public Port<MainConfig> getConfigPort() {
        return configPort;
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        readTextFile.setErrorPort(port);
        parseFlowFile.setErrorPort(port);
        handleParserResult.setErrorPort(port);
        outputAllFormats.setErrorPort(port);
        fillPortPairs.setErrorPort(port);
        fillTemplate.setErrorPort(port);
        createOutputFileName.setErrorPort(port);
        writeTextFile.setErrorPort(port);
    }
}
