package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.SemanticConnectionsData;
import org.flowdev.flowparser.util.SemanticConnectionsUtil;

import static org.flowdev.flowparser.util.PortUtil.copyPort;
import static org.flowdev.flowparser.util.PortUtil.defaultOutPort;
import static org.flowdev.flowparser.util.SemanticConnectionsUtil.addPort;
import static org.flowdev.flowparser.util.SemanticConnectionsUtil.correctFromPort;

public class HandleChainEnd extends FilterOp<SemanticConnectionsData, NoConfig> {
    @Override
    protected void filter(SemanticConnectionsData data) {
        Connection chainEnd = data.chainEnd();
        AddOpResult addOpResult = data.addOpResult();
        Operation lastOp = addOpResult.op();

        if (chainEnd != null) {
            chainEnd.fromOp(lastOp);
            if (addOpResult.outPort() != null) {
                chainEnd.fromPort(addOpResult.outPort());
            }
            if (chainEnd.fromPort().name() == null && chainEnd.toPort().name() == null) {
                chainEnd.fromPort(defaultOutPort(chainEnd.fromPort().srcPos()));
                chainEnd.toPort(copyPort(chainEnd.fromPort(), chainEnd.toPort().srcPos()));
            } else if (chainEnd.toPort().name() == null) {
                chainEnd.toPort(copyPort(chainEnd.fromPort(), chainEnd.toPort().srcPos()));
            } else if (chainEnd.fromPort() == null) {
                chainEnd.fromPort(defaultOutPort(chainEnd.fromPort().srcPos()));
                addPort(lastOp, chainEnd.fromPort(), SemanticConnectionsUtil.TYPE_OUTPUT, data.mainData().parserData(), addOpResult);
            }
            correctFromPort(chainEnd, addOpResult.op());
            data.conns().add(chainEnd);
        } else if (addOpResult.outPortAdded()) {
            lastOp.outPorts().remove(addOpResult.op().outPorts().size() - 1);
        }

        outPort.send(data);
    }
}
