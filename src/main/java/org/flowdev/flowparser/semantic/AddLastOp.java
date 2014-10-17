package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.SemanticConnectionsData;
import org.flowdev.parser.data.ParserData;

import java.util.Map;

import static org.flowdev.flowparser.util.SemanticConnectionsUtil.TYPE_OUTPUT;
import static org.flowdev.flowparser.util.SemanticConnectionsUtil.addPort;
import static org.flowdev.parser.util.ParserUtil.addSemanticError;

public class AddLastOp extends FilterOp<SemanticConnectionsData, NoConfig> {
    @Override
    protected void filter(SemanticConnectionsData data) {
        Map<String, Operation> ops = data.ops();
        Operation newOp = data.newOp();
        ParserData parserData = data.mainData().parserData();
        AddOpResult result = data.addOpResult();

        Operation existingOp = ops.get(newOp.name());
        if (existingOp != null) {
            updateExistingOp(existingOp, newOp, parserData, result);
        } else {
            addNewOp(ops, newOp, result);
        }

        outPort.send(data);
    }

    private void updateExistingOp(Operation existingOp, Operation newOp, ParserData parserData, AddOpResult result) {
        if (existingOp.type() == null) {
            existingOp.type(newOp.type());
        } else if (newOp.type() != null && !newOp.type().equals(existingOp.type())) {
            addSemanticError(parserData, newOp.srcPos(), "The operation '" + newOp.name() +
                    "' has got two different types '" + existingOp.type() + "' and '" + newOp.type() + "'!");
        }
        if (newOp.inPorts().size() > 0) {
            addPort(existingOp, newOp.inPorts().get(0), "input", parserData, null);
        }
        if (newOp.outPorts().size() > 0) {
            addPort(existingOp, newOp.outPorts().get(0), TYPE_OUTPUT, parserData, result);
        }
        result.op(existingOp);
    }

    private void addNewOp(Map<String, Operation> ops, Operation newOp, AddOpResult result) {
        ops.put(newOp.name(), newOp);
        if (newOp.outPorts().size() > 0) {
            result.outPort(newOp.outPorts().get(0)).outPortAdded(true);
        }
        result.op(newOp);
    }
}
