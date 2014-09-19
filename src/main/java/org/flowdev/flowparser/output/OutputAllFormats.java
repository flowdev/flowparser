package org.flowdev.flowparser.output;

import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.MainData;

import java.util.List;

/**
 * Iterate over all requested formats and output them all.
 */
public class OutputAllFormats extends FilterOp<MainData, OutputAllFormatsConfig> {
    @Override
    protected void filter(MainData data) {
        List<String> formats = getVolatileConfig().formats;
        for (String format : formats) {
            data.format(format);
            outPort.send(data);
        }
    }
}
