package org.flowdev.flowparser.output;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.MainData;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.flowparser.rawdata.RawDataType;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class FillTemplate extends FilterOp<MainData, NoConfig> {
    private static final String TEMPLATE_DIR = FillTemplate.class.getPackage().getName().replace('.', '/') + "/";

    @Override
    protected void filter(MainData data) {
        MustacheFactory mf = new DefaultMustacheFactory(
                TEMPLATE_DIR + data.format);
        Mustache standardTpl = mf.compile("template.mustache");
        Mustache linearTpl = null;
        try {
            linearTpl = mf.compile("linear.mustache");
        } catch (UncheckedExecutionException uee) {
            // MustacheException because of missing template is ignored
            if (!(uee.getCause() instanceof MustacheException)) {
                throw uee;
            }
        }
        StringBuilder fileContent = new StringBuilder(80192);
        for (Flow flow : data.flowFile.getFlows()) {
            StringWriter sw = new StringWriter();
            if (linearTpl != null && isLinearFlow(flow)) {
                linearTpl.execute(sw, flow);
            } else {
                standardTpl.execute(sw, flow);
            }
            sw.flush();
            fileContent.append(sw.toString());
        }

        data.fileContent = fileContent.toString();
        outPort.send(data);
    }

    private static boolean isLinearFlow(Flow flow) {
        for (Operation op : flow.getOperations()) {
            if (!isFilterOp(op)) {
                return false;
            }
        }
        if (flow.getConnections().size() != flow.getOperations().size() + 1) {
            return false;
        }
        List<String> connectedOps = new ArrayList<>(flow.getConnections().size());
        RawDataType dataType = new RawDataType();
        for (Connection connection : flow.getConnections()) {
            if (!isLinearConnection(connection, connectedOps, dataType)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFilterOp(Operation op) {
        if (op.getPorts().size() != 1) {
            return false;
        }
        PortPair portPair = op.getPorts().get(0);
        return "in".equals(portPair.getInPort()) && "out".equals(portPair.getOutPort());
    }

    private static boolean isLinearConnection(Connection connection, List<String> connectedOps, RawDataType dataType) {
        String newOp;
        if (connectedOps.isEmpty()) {
            if (connection.getFromOp() != null) {
                return false;
            }
            if (!"in".equals(connection.getFromPort())) {
                return false;
            }
            if (!"in".equals(connection.getToPort())) {
                return false;
            }
            if (connection.getToOp() == null) {
                return false;
            }
            newOp = connection.getToOp();
        } else if (connection.getToOp() == null) {
            if (!connectedOps.get(connectedOps.size() - 1).equals(connection.getFromOp())) {
                return false;
            }
            if (!"out".equals(connection.getFromPort())) {
                return false;
            }
            if (!"out".equals(connection.getToPort())) {
                return false;
            }
            newOp = "<NULL>!!!";
        } else {
            if (!connectedOps.get(connectedOps.size() - 1).equals(connection.getFromOp())) {
                return false;
            }
            if (!"out".equals(connection.getFromPort())) {
                return false;
            }
            if (!"in".equals(connection.getToPort())) {
                return false;
            }
            if (connectedOps.contains(connection.getToOp())) {
                return false;
            }
            newOp = connection.getToOp();
        }

        if (!conform(connection.getDataType(), dataType)) {
            return false;
        }
        connectedOps.add(newOp);
        return true;
    }

    private static boolean conform(String s, RawDataType dataType) {
        if (s == null) {
            return true;
        } else if (dataType.getType() == null) {
            dataType.setType(s);
        }
        return s.equals(dataType.getType());
    }
}
