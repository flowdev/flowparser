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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class FillTemplate extends FilterOp<MainData, NoConfig> {
    private static final String TEMPLATE_DIR = FillTemplate.class.getPackage().getName().replace('.', '/') + "/";

    @Override
    protected void filter(MainData data) {
        MustacheFactory mf = new DefaultMustacheFactory(
                TEMPLATE_DIR + data.format());
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
        for (Flow flow : data.flowFile().flows()) {
            StringWriter sw = new StringWriter();
            if (linearTpl != null && isLinearFlow(flow)) {
                linearTpl.execute(sw, flow);
            } else {
                standardTpl.execute(sw, flow);
            }
            sw.flush();
            fileContent.append(sw.toString());
        }

        data.outputContent(fileContent.toString());
        outPort.send(data);
    }

    private static boolean isLinearFlow(Flow flow) {
        for (Operation op : flow.operations()) {
            if (!isFilterOp(op)) {
                return false;
            }
        }
        if (flow.connections().size() != flow.operations().size() + 1) {
            return false;
        }
        List<String> connectedOps = new ArrayList<>(flow.connections().size());
        Connection dataType = new Connection();
        for (Connection connection : flow.connections()) {
            if (!isLinearConnection(connection, connectedOps, dataType)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFilterOp(Operation op) {
        if (op.ports().size() != 1) {
            return false;
        }
        PortPair portPair = op.ports().get(0);
        return "in".equals(portPair.inPort().name()) && "out".equals(portPair.outPort().name());
    }

    private static boolean isLinearConnection(Connection connection, List<String> connectedOps, Connection dataType) {
        String newOp;
        if (connectedOps.isEmpty()) {
            if (connection.fromOp() != null) {
                return false;
            }
            if (!"in".equals(connection.fromPort().name())) {
                return false;
            }
            if (!"in".equals(connection.toPort().name())) {
                return false;
            }
            if (connection.toOp() == null) {
                return false;
            }
            newOp = connection.toOp().name();
        } else if (connection.toOp() == null) {
            if (!connectedOps.get(connectedOps.size() - 1).equals(connection.fromOp().name())) {
                return false;
            }
            if (!"out".equals(connection.fromPort().name())) {
                return false;
            }
            if (!"out".equals(connection.toPort().name())) {
                return false;
            }
            newOp = "<NULL>!!!";
        } else {
            if (!connectedOps.get(connectedOps.size() - 1).equals(connection.fromOp().name())) {
                return false;
            }
            if (!"out".equals(connection.fromPort().name())) {
                return false;
            }
            if (!"in".equals(connection.toPort().name())) {
                return false;
            }
            if (connectedOps.contains(connection.toOp().name())) {
                return false;
            }
            newOp = connection.toOp().name();
        }

        if (!conform(connection.dataType(), dataType)) {
            return false;
        }
        connectedOps.add(newOp);
        return true;
    }

    private static boolean conform(String s, Connection dataType) {
        if (s == null) {
            return true;
        } else if (dataType.dataType() == null) {
            dataType.dataType(s);
        }
        return s.equals(dataType.dataType());
    }
}
