package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.op.io.ReadTextFile;
import org.flowdev.base.op.io.WriteTextFile;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.output.CreateOutputFileName;

/**
 * This flow parses a flow file and creates one ore more output files.
 */
public class MainFlow implements IMainFlow {
    private ReadTextFile<MainData, MainData> readTextFile;
    private CoreFlow coreFlow;
    private CreateOutputFileName createOutputFileName;
    private WriteTextFile<MainData> writeTextFile;


    public MainFlow() {
        ReadTextFile.Params<MainData, MainData> readTextFileParams = new ReadTextFile.Params<>();
        readTextFileParams.getFileName = data -> data.parserData().source().name();
        readTextFileParams.setFileContent = (data, subdata) -> {
            data.parserData().source().content(subdata);
            return data;
        };
        readTextFile = new ReadTextFile<>(readTextFileParams);

        coreFlow = new CoreFlow();

        createOutputFileName = new CreateOutputFileName();

        WriteTextFile.Params<MainData> writeTextFileParams = new WriteTextFile.Params<>();
        writeTextFileParams.getFileContent = MainData::outputContent;
        writeTextFileParams.getFileName = MainData::outputName;
        writeTextFile = new WriteTextFile<>(writeTextFileParams);

        createConnections();
    }

    private void createConnections() {
        readTextFile.setOutPort(coreFlow.getInPort());
        coreFlow.setOutPort(createOutputFileName.getInPort());
        createOutputFileName.setOutPort(writeTextFile.getInPort());
    }

    @Override
    public Port<MainData> getInPort() {
        return readTextFile.getInPort();
    }

    @Override
    public Port<CoreFlow.CoreFlowConfig> getConfigPort() {
        return coreFlow.getConfigPort();
    }

    @Override
    public void setErrorPort(Port<Throwable> port) {
        readTextFile.setErrorPort(port);
        coreFlow.setErrorPort(port);
        createOutputFileName.setErrorPort(port);
        writeTextFile.setErrorPort(port);
    }
}
