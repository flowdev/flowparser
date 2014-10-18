package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.BaseOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.SemanticConnectionsData;

import static org.flowdev.flowparser.util.SemanticConnectionsUtil.correctToPort;

public class HandleChainBeg extends BaseOp<NoConfig> {
    private Port<SemanticConnectionsData> inPort = this::handleChainBeg;
    private Port<SemanticConnectionsData> outPort;
    private Port<SemanticConnectionsData> addOpInPort = data -> this.dataFromAddOp = data;
    private Port<SemanticConnectionsData> addOpOutPort;

    private SemanticConnectionsData dataFromAddOp;


    private void handleChainBeg(SemanticConnectionsData data) {
        Connection connBeg = (Connection) data.chainBeg().get(0);
        Operation opBeg = (Operation) data.chainBeg().get(1);

        // first add the operation:
        data.newOp(opBeg);
        addOpOutPort.send(data);
        data = dataFromAddOp;
        Operation lastOp = data.addOpResult().op();

        // now add the connection if it exists:
        if (connBeg != null) {
            connBeg.toOp(lastOp);
            correctToPort(connBeg, lastOp);
            data.conns().add(connBeg);
        }

        outPort.send(data);
    }

    public Port<SemanticConnectionsData> getAddOpInPort() {
        return addOpInPort;
    }

    public void setAddOpOutPort(Port<SemanticConnectionsData> addOpOutPort) {
        this.addOpOutPort = addOpOutPort;
    }

    public Port<SemanticConnectionsData> getInPort() {
        return inPort;
    }

    public void setOutPort(Port<SemanticConnectionsData> outPort) {
        this.outPort = outPort;
    }

}
