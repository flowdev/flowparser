package org.flowdev.flowparser.output;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.*;
import org.flowdev.flowparser.rawdata.RawDataType;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class FillTemplate<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
        public Getter<T, String> getFormat;
        public Getter<T, FlowFile> getFlowFile;
        public Setter<String, T, T> setFileContent;
    }
    static final String TEMPLATE_DIR = FillTemplate.class.getPackage().getName().replace('.', '/') + "/";

    private final Params<T> params;

    public FillTemplate(Params<T> params) {
        this.params = params;
    }

    @Override
    protected T filter(T data) {
        String format = params.getFormat.get(data);
        FlowFile flowFile = params.getFlowFile.get(data);

        MustacheFactory mf = new DefaultMustacheFactory(
                TEMPLATE_DIR + format);
        Mustache standardTpl = mf.compile("template.mustache");
        Mustache linearTpl = null;
        try {
            linearTpl = mf.compile("linear.mustache");
        } catch (UncheckedExecutionException uee) {
            // MustacheException because of missing template is ignored
            if (! (uee.getCause() instanceof MustacheException)) {
                throw uee;
            }
        }
        StringBuilder fileContent = new StringBuilder(80192);
        for (Flow flow : flowFile.flows) {
            StringWriter sw = new StringWriter();
            if (linearTpl != null && isLinearFlow(flow)) {
                linearTpl.execute(sw, flow);
            } else {
                standardTpl.execute(sw, flow);
            }
            sw.flush();
            fileContent.append(sw.toString());
        }

        params.setFileContent.set(data, fileContent.toString());
        return data;
    }

    private static boolean isLinearFlow(Flow flow) {
        for (Operation op : flow.operations) {
            if (! isFilterOp(op)) {
                return false;
            }
        }
        if (flow.connections.size() != flow.operations.size() + 1) {
            return false;
        }
        List<String> connectedOps = new ArrayList<>(flow.connections.size());
        RawDataType dataType = new RawDataType();
        for (Connection connection : flow.connections) {
            if (! isLinearConnection(connection, connectedOps, dataType)) {
                return false;
            }
        }
        return true;
    }
    private static boolean isFilterOp(Operation op) {
        if (op.ports.size() != 1) {
            return false;
        }
        PortPair portPair = op.ports.get(0);
        return "in".equals(portPair.inPort) && "out".equals(portPair.outPort);
    }
    private static boolean isLinearConnection(Connection connection, List<String> connectedOps, RawDataType dataType) {
        String newOp;
        if (connectedOps.isEmpty()) {
            if (connection.fromOp != null) {
                return false;
            }
            if (! "in".equals(connection.fromPort)) {
                return false;
            }
            if (! "in".equals(connection.toPort)) {
                return false;
            }
            if (connection.toOp == null) {
                return false;
            }
            newOp = connection.toOp;
        } else if (connection.toOp == null) {
            if (! connectedOps.get(connectedOps.size() - 1).equals(connection.fromOp)) {
                return false;
            }
            if (! "out".equals(connection.fromPort)) {
                return false;
            }
            if (! "out".equals(connection.toPort)) {
                return false;
            }
            newOp = "<NULL>!!!";
        } else {
            if (! connectedOps.get(connectedOps.size() - 1).equals(connection.fromOp)) {
                return false;
            }
            if (! "out".equals(connection.fromPort)) {
                return false;
            }
            if (! "in".equals(connection.toPort)) {
                return false;
            }
            if (connectedOps.contains(connection.toOp)) {
                return false;
            }
            newOp = connection.toOp;
        }

        if (! conform(connection.dataType, dataType)) {
            return false;
        }
        connectedOps.add(newOp);
        return true;
    }
    private static boolean conform(String s, RawDataType dataType) {
        if (s == null) {
            return true;
        } else if (dataType.type ==  null) {
            dataType.type = s;
        }
        return s.equals(dataType.type);
    }
}
