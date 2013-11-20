package org.flowdev.flowparser;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.op.io.ReadTextFile;
import org.flowdev.base.op.io.WriteTextFile;
import org.flowdev.flowparser.cook.CookFlowFile;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.output.CreateOutputFileName;
import org.flowdev.flowparser.output.FillTemplate;
import org.flowdev.flowparser.output.OutputAllFormats;
import org.flowdev.flowparser.output.OutputAllFormatsConfig;
import org.flowdev.flowparser.rawdata.RawFlowFile;

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
    private final Port<MainConfig> configPort = new Port<MainConfig>() {
        @Override
        public void send(MainConfig data) {
            if (data.outputAllFormats != null) {
                outputAllFormats.getConfigPort().send(data.outputAllFormats);
            }
        }
    };

    public MainFlow() {
        ReadTextFile.Params<MainData, MainData> readTextFileParams = new ReadTextFile.Params<>();
        readTextFileParams.getFileName = new Getter<MainData, String>() {
            @Override
            public String get(MainData data) {
                return data.fileName;
            }
        };
        readTextFileParams.setFileContent = new
                Setter<String, MainData, MainData>() {
                    @Override
                    public MainData set(MainData data, String subdata) {
                        data.fileContent = subdata;
                        return data;
                    }
                };
        readTextFile = new ReadTextFile<>(readTextFileParams);

        ParseToRawFlowFile.Params<MainData> parseToRawFlowFileParams = new ParseToRawFlowFile.Params<>();
        parseToRawFlowFileParams.getFileName = new
                Getter<MainData, String>() {
                    @Override
                    public String get(MainData data) {
                        return data.fileName;
                    }
                };
        parseToRawFlowFileParams.getFileContent = new
                Getter<MainData, String>() {
                    @Override
                    public String get(MainData data) {
                        return data.fileContent;
                    }
                };
        parseToRawFlowFileParams.setFlowFile = new
                Setter<RawFlowFile, MainData, MainData>() {
                    @Override
                    public MainData set(MainData data, RawFlowFile subdata) {
                        data.rawFlowFile = subdata;
                        return data;
                    }
                };
        parseToRawFlowFile = new ParseToRawFlowFile<>(parseToRawFlowFileParams);

        CookFlowFile.Params<MainData> cookFlowFileParams = new CookFlowFile.Params<>();
        cookFlowFileParams.getFileName = new
                Getter<MainData, String>() {
                    @Override
                    public String get(MainData data) {
                        return data.fileName;
                    }
                };
        cookFlowFileParams.getRawFlowFile = new
                Getter<MainData, RawFlowFile>() {
                    @Override
                    public RawFlowFile get(MainData data) {
                        return data.rawFlowFile;
                    }
                };
        cookFlowFileParams.setCookedFlowFile = new
                Setter<FlowFile, MainData, MainData>() {
                    @Override
                    public MainData set(MainData data, FlowFile subdata) {
                        data.flowFile = subdata;
                        return data;
                    }
                };
        cookFlowFile = new CookFlowFile<>(cookFlowFileParams);

        OutputAllFormats.Params<MainData> outputAllFormatsParams = new OutputAllFormats.Params<>();
        outputAllFormatsParams.setFormat = new Setter<String, MainData, MainData>() {
            @Override
            public MainData set(MainData data, String subdata) {
                data.format = subdata;
                return data;
            }
        };
        outputAllFormats = new OutputAllFormats<>(outputAllFormatsParams);

        FillTemplate.Params<MainData> fillTemplateParams = new FillTemplate.Params<>();
        fillTemplateParams.getFormat = new Getter<MainData, String>() {
            @Override
            public String get(MainData data) {
                return data.format;
            }
        };
        fillTemplateParams.getFlowFile = new Getter<MainData, FlowFile>() {
            @Override
            public FlowFile get(MainData data) {
                return data.flowFile;
            }
        };
        fillTemplateParams.setFileContent = new Setter<String, MainData, MainData>() {
            @Override
            public MainData set(MainData data, String subdata) {
                data.fileContent = subdata;
                return data;
            }
        };
        fillTemplate = new FillTemplate<>(fillTemplateParams);

        CreateOutputFileName.Params<MainData> createOutputFileNameParams = new CreateOutputFileName.Params<>();
        createOutputFileNameParams.getFileName = new Getter<MainData, String>() {
            @Override
            public String get(MainData data) {
                return data.flowFile.fileName;
            }
        };
        createOutputFileNameParams.getFormat = new Getter<MainData, String>() {
            @Override
            public String get(MainData data) {
                return data.format;
            }
        };
        createOutputFileNameParams.setFileName = new Setter<String, MainData, MainData>() {
            @Override
            public MainData set(MainData data, String subdata) {
                data.fileName = subdata;
                return data;
            }
        };
        createOutputFileName = new CreateOutputFileName<>(createOutputFileNameParams);

        WriteTextFile.Params<MainData> writeTextFileParams = new WriteTextFile.Params<>();
        writeTextFileParams.getFileContent = new Getter<MainData, String>() {
            @Override
            public String get(MainData data) {
                return data.fileContent;
            }
        };
        writeTextFileParams.getFileName = new Getter<MainData, String>() {
            @Override
            public String get(MainData data) {
                return data.fileName;
            }
        };
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
