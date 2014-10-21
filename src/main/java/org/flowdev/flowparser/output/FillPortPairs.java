package org.flowdev.flowparser.output;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.max;

public class FillPortPairs extends FilterOp<MainData, NoConfig> {
    @Override
    protected void filter(MainData data) {
        for (Flow flow : data.flowFile().flows()) {
            flow.operations().forEach(this::fillPortPairs4Op);
        }
        outPort.send(data);
    }

    private void fillPortPairs4Op(Operation op) {
        int n = max(op.inPorts().size(), op.outPorts().size());
        List<PortPair> portPairs = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            PortPair portPair = new PortPair();
            if (i < op.inPorts().size()) {
                portPair.inPort(op.inPorts().get(i));
            }
            if (i < op.outPorts().size()) {
                portPair.outPort(op.outPorts().get(i));
            }
            portPairs.add(portPair);
        }
        portPairs.get(n - 1).isLast(true);
        op.portPairs(portPairs);
    }
}
