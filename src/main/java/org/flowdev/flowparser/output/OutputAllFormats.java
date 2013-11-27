package org.flowdev.flowparser.output;

import org.flowdev.base.Setter;
import org.flowdev.base.op.Filter;

import java.util.List;

/**
 * Iterate over all requested formats and output them all.
 */
public class OutputAllFormats<T> extends Filter<T, OutputAllFormatsConfig> {
    public static class Params<T> {
        public Setter<String, T, T> setFormat;
    }

    private final Params<T> params;

    public OutputAllFormats(Params<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        List<String> formats = getVolatileConfig().formats;
        for (String format : formats) {
            params.setFormat.set(data, format);
            outPort.send(data);
        }
    }
}
