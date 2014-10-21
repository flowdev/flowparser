package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.*;
import org.flowdev.parser.data.ParserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.flowdev.parser.util.ParserUtil.addSemanticError;


public class VerifyOutPortsUsedOnlyOnce extends FilterOp<MainData, NoConfig> {
    @Override
    protected void filter(MainData data) {
        ParserData parserData = data.parserData();
        Flow result = (Flow) parserData.result().value();

        if (result != null) {
            // check for output ports that are connected to multiple input ports:
            Map<String, Set<OpPort>> connMap = new HashMap<>(256);
            for (Connection conn : result.connections()) {
                String fromPort = stringifyPort(conn.fromOp(), conn.fromPort());
                OpPort toPort = new OpPort(conn.toOp(), conn.toPort());
                Set<OpPort> toPorts = connMap.get(fromPort);
                if (toPorts == null) {
                    toPorts = new HashSet<>();
                    connMap.put(fromPort, toPorts);
                }
                toPorts.add(toPort);
            }

            for (Map.Entry<String, Set<OpPort>> entry : connMap.entrySet()) {
                if (entry.getValue().size() > 1) {
                    addSemanticError(parserData, lastSrcPos(entry.getValue()), "The output port '" + entry.getKey() +
                            "' is connected to multiple input ports " + stringifyPorts(entry.getValue()) + "!");
                }
            }
        }

        outPort.send(data);
    }

    private int lastSrcPos(Set<OpPort> ports) {
        int srcPos = -1;
        for (OpPort port : ports) {
            if (port.port.srcPos() > srcPos) {
                srcPos = port.port.srcPos();
            }
        }
        return srcPos;
    }

    private String stringifyPorts(Set<OpPort> ports) {
        StringBuilder sb = new StringBuilder(4096);
        for (OpPort port : ports) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(stringifyPort(port.op, port.port));
        }
        return sb.toString();
    }

    private String stringifyPort(Operation op, PortData port) {
        StringBuilder sb = new StringBuilder(128);
        if (op == null) {
            sb.append("<FLOW>");
        } else {
            sb.append(op.name());
        }
        sb.append(':').append(port.name());
        if (port.hasIndex()) {
            sb.append('.').append(port.index());
        }
        return sb.toString();
    }

    private class OpPort {
        private Operation op;
        private PortData port;
        private String portDesc;

        private OpPort(Operation op, PortData port) {
            this.op = op;
            this.port = port;
            this.portDesc = stringifyPort(op, port);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OpPort)) return false;

            OpPort port = (OpPort) o;

            if (!portDesc.equals(port.portDesc)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return portDesc.hashCode();
        }
    }
}
