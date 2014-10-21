package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.BaseOp;
import org.flowdev.flowparser.data.MainData;

public class SemanticConnections extends BaseOp<NoConfig> {
    private final CreateConnections createConns;
    private final VerifyOutPortsUsedOnlyOnce verifyOutPorts;
    private final HandleChainBeg handleChainBeg;
    private final HandleChainMids handleChainMids;
    private final HandleChainEnd handleChainEnd;
    private final AddLastOp begAddLastOp;
    private final AddLastOp midAddLastOp;

    public SemanticConnections() {
        createConns = new CreateConnections();
        verifyOutPorts = new VerifyOutPortsUsedOnlyOnce();
        handleChainBeg = new HandleChainBeg();
        handleChainMids = new HandleChainMids();
        handleChainEnd = new HandleChainEnd();
        begAddLastOp = new AddLastOp();
        midAddLastOp = new AddLastOp();

        createConnections();
        initConfig();
    }

    private void createConnections() {
        createConns.setOutPort(verifyOutPorts.getInPort());
        createConns.setChainOutPort(handleChainBeg.getInPort());
        handleChainBeg.setOutPort(handleChainMids.getInPort());
        handleChainMids.setOutPort(handleChainEnd.getInPort());
        handleChainEnd.setOutPort(createConns.getChainInPort());
        handleChainBeg.setAddOpOutPort(begAddLastOp.getInPort());
        begAddLastOp.setOutPort(handleChainBeg.getAddOpInPort());
        handleChainMids.setAddOpOutPort(midAddLastOp.getInPort());
        midAddLastOp.setOutPort(handleChainMids.getAddOpInPort());
    }

    private void initConfig() {
    }

    public Port<MainData> getInPort() {
        return createConns.getInPort();
    }

    public void setOutPort(Port<MainData> port) {
        verifyOutPorts.setOutPort(port);
    }

    public void setErrorPort(Port<Throwable> port) {
        createConns.setErrorPort(port);
        verifyOutPorts.setErrorPort(port);
        handleChainBeg.setErrorPort(port);
        handleChainMids.setErrorPort(port);
        handleChainEnd.setErrorPort(port);
        begAddLastOp.setErrorPort(port);
        midAddLastOp.setErrorPort(port);
    }
}
