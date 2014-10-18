package org.flowdev.flowparser.util;

import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.flowparser.semantic.connections.AddOpResult;
import org.flowdev.parser.data.ParserData;

import java.util.List;

import static java.lang.Integer.max;
import static org.flowdev.flowparser.util.PortUtil.equalPorts;
import static org.flowdev.parser.util.ParserUtil.addSemanticError;

public abstract class SemanticConnectionsUtil {
    public static final String TYPE_OUTPUT = "output";

    public static void correctFromPort(Connection conn, Operation op) {
        for (PortData port : op.outPorts()) {
            Boolean eq = equalPorts(port, conn.fromPort());
            if (eq == null || eq) {
                conn.fromPort(port);
            }
        }
    }

    public static void correctToPort(Connection conn, Operation op) {
        for (PortData port : op.inPorts()) {
            Boolean eq = equalPorts(port, conn.toPort());
            if (eq == null || eq) {
                conn.toPort(port);
            }
        }
    }

    public static void addPort(Operation op, PortData newPort, String portType, ParserData parserData, AddOpResult result) {
        List<PortData> ports = (result == null) ? op.inPorts() : op.outPorts();
        for (PortData oldPort : ports) {
            Boolean eq = equalPorts(oldPort, newPort);
            if (eq == null) {
                addSemanticError(parserData, max(newPort.srcPos(), oldPort.srcPos()),
                        "The " + portType + " port '" + newPort.name() + "' of the operation '" + op.name()
                                + "' is used as indexed and unindexed port in the same flow!");
                return;
            }
            if (eq) {
                if (result != null) {
                    result.outPort(oldPort).outPortAdded(false);
                }
                return;
            }
        }

        ports.add(newPort);
        if (result != null) {
            result.outPort(newPort).outPortAdded(true);
        }
    }
}
