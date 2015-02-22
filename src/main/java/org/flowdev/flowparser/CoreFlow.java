package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.output.FillPortPairs;
import org.flowdev.flowparser.output.FillTemplate;
import org.flowdev.flowparser.output.OutputAllFormats;
import org.flowdev.flowparser.parse.HandleParserResult;
import org.flowdev.flowparser.parse.ParseFlowFile;
import org.flowdev.parser.op.ParserParams;

import static org.flowdev.flowparser.output.OutputAllFormats.OutputAllFormatsConfig;
import static org.flowdev.flowparser.util.FormatUtil.formatsAsList;

/**
 * This flow parses a flow file and creates one ore more output files.
 */
public class CoreFlow {
    private ParseFlowFile parseFlowFile;
    private HandleParserResult handleParserResult;
    private OutputAllFormats outputAllFormats;
    private FillPortPairs fillPortPairs;
    private FillTemplate fillTemplate;
    private Port<CoreFlowConfig> configPort = data -> outputAllFormats.getConfigPort().send(data.outputAllFormats());


    public CoreFlow() {
        ParserParams<MainData> parserParams = new ParserParams<>();
        parserParams.getParserData = MainData::parserData;
        parserParams.setParserData = MainData::parserData;
        parseFlowFile = new ParseFlowFile(parserParams);

        handleParserResult = new HandleParserResult();

        outputAllFormats = new OutputAllFormats();

        fillPortPairs = new FillPortPairs();

        fillTemplate = new FillTemplate();

        createConnections();
        initConfig();
    }

    private void createConnections() {
        parseFlowFile.setOutPort(handleParserResult.getInPort());
        handleParserResult.setOutPort(outputAllFormats.getInPort());
        outputAllFormats.setFormatPort(0, fillPortPairs.getInPort());
        outputAllFormats.setFormatPort(1, fillTemplate.getInPort());
        outputAllFormats.setFormatPort(2, fillPortPairs.getInPort());
        fillPortPairs.setOutPort(fillTemplate.getInPort());
    }

    private void initConfig() {
        outputAllFormats.getConfigPort().send(new OutputAllFormatsConfig().formats(formatsAsList()));
    }

    public Port<MainData> getInPort() {
        return parseFlowFile.getInPort();
    }

    public Port<CoreFlowConfig> getConfigPort() {
        return configPort;
    }

    public void setErrorPort(Port<Throwable> port) {
        parseFlowFile.setErrorPort(port);
        handleParserResult.setErrorPort(port);
        outputAllFormats.setErrorPort(port);
        fillPortPairs.setErrorPort(port);
        fillTemplate.setErrorPort(port);
    }

    public void setOutPort(Port<MainData> outPort) {
        fillTemplate.setOutPort(outPort);
    }

    public static class CoreFlowConfig {
        private OutputAllFormatsConfig outputAllFormats;

        public OutputAllFormatsConfig outputAllFormats() {
            return this.outputAllFormats;
        }

        public CoreFlowConfig outputAllFormats(final OutputAllFormatsConfig outputAllFormats) {
            this.outputAllFormats = outputAllFormats;
            return this;
        }
    }
}
