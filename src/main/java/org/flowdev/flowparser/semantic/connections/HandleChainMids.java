package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.Port;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.BaseOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.flowparser.data.SemanticConnectionsData;

import java.util.List;

import static org.flowdev.flowparser.util.SemanticConnectionsUtil.correctToPort;

public class HandleChainMids extends BaseOp<NoConfig> {
    private Port<SemanticConnectionsData> inPort = this::handleChainMids;
    private Port<SemanticConnectionsData> outPort;
    private Port<SemanticConnectionsData> addOpInPort = data -> this.dataFromAddOp = data;
    private Port<SemanticConnectionsData> addOpOutPort;

    private SemanticConnectionsData dataFromAddOp;


    @SuppressWarnings("unchecked")
    private void handleChainMids(SemanticConnectionsData data) {
        Operation fromOp = data.addOpResult().op();

        if (data.chainMids() != null) {
            for (Object chainMidObj : data.chainMids()) {
                List<Object> chainMid = (List<Object>) chainMidObj;
                String arrowType = (String) chainMid.get(0);
                PortData fromPort = data.addOpResult().outPort();
                Operation toOp = (Operation) chainMid.get(1);
                PortData toPort = toOp.inPorts().get(0);

                // add the operation:
                data.newOp(toOp);
                addOpOutPort.send(data);
                data = dataFromAddOp;
                toOp = data.addOpResult().op();

                // now add the connection:
                Connection connMid = new Connection().dataType(arrowType).showDataType(arrowType != null)
                        .fromOp(fromOp).fromPort(fromPort).toOp(toOp).toPort(toPort);
                correctToPort(connMid, toOp);
                data.conns().add(connMid);

                fromOp = toOp;
            }
        }


        dataFromAddOp = null;
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
