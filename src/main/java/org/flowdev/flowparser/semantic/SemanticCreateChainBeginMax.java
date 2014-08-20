package org.flowdev.flowparser.semantic;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.parser.op.ParserParams;

public class SemanticCreateChainBeginMax<T> extends FilterOp<T, NoConfig> {
    private ParserParams<T> params;

    public SemanticCreateChainBeginMax(ParserParams<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {

    }
}
