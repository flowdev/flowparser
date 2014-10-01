package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;

import java.util.List;

import static org.flowdev.parser.util.ParserUtil.addError;

public class SemanticCreateOperationNameParens<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticCreateOperationNameParens(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        ParserData parserData = params.getParserData.get(data);

        parserData.result().value(createOperationNameParens(parserData));

        outPort.send(params.setParserData.set(data, parserData));
    }

    @SuppressWarnings("unchecked")
    private Operation createOperationNameParens(ParserData parserData) {
        Operation op = new Operation().srcPos(parserData.source().pos());
        List<Object> opName = (List<Object>) parserData.subResults().get(0).value();

        if (opName != null) {
            op.name((String) opName.get(0));
        }
        op.type((String) parserData.subResults().get(3).value());
        if (op.name() == null && op.type() == null) {
            op = null;
            addError(parserData, "At least an operation name or an operation type have to be provided.");
        } else if (op.name() == null) {
            // set default name if none is given:
            op.name(op.type().substring(0, 1).toLowerCase() + op.type().substring(1));
        }
        return op;
    }
}
