package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.parser.op.ParserParams;

public class SemanticCreateChainBeginMin<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticCreateChainBeginMin(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {

    }
}
