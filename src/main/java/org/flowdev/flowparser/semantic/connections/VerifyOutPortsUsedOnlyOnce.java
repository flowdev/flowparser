package org.flowdev.flowparser.semantic.connections;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.*;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.util.ParserUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class VerifyOutPortsUsedOnlyOnce extends FilterOp<MainData, NoConfig> {
    @Override
    protected void filter(MainData data) {
        ParserData parserData = data.parserData();
        Flow result = (Flow) parserData.result().value();

        if (result != null) {
            // check for output ports that are connected to multiple input ports:
            Map<String, Set<String>> connMap = new HashMap<>(256);
            for (Connection conn : result.connections()) {
                String fromPort = stringifyPort(conn.fromOp(), conn.fromPort());
                String toPort = stringifyPort(conn.toOp(), conn.toPort());
                Set<String> toPorts = connMap.get(fromPort);
                if (toPorts == null) {
                    toPorts = new HashSet<>();
                    connMap.put(fromPort, toPorts);
                }
                toPorts.add(toPort);
            }

            for (Map.Entry<String, Set<String>> entry : connMap.entrySet()) {
                if (entry.getValue().size() > 1) {
                    ParserUtil.addError(parserData, "The output port '" + entry.getKey() +
                            "' is connected to multiple input ports " + entry.getValue().toString() + "!");
                }
            }
        }

        outPort.send(data);
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
}
