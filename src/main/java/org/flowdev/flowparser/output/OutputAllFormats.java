package org.flowdev.flowparser.output;

import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.op.Configure;

import java.util.List;

/**
 * Iterate over all requested formats and output them all.
 */
public class OutputAllFormats<T> extends Configure<OutputAllFormatsConfig> {
    public static class Params<T> {
        public Setter<String, T, T> setFormat;
    }

    private final Params<T> params;
    private Port<T> outPort;
    private final Port<T> inPort = new Port<T>() {
        @Override
        public void send(T data) {
            outputFormats(data);
        }
    };

    private void outputFormats(T data) {
        List<String> formats = getVolatileConfig().formats;
        for (String format : formats) {
            params.setFormat.set(data, format);
            outPort.send(data);
        }
    }

    public OutputAllFormats(Params<T> params) {
        this.params = params;
    }

    public Port<T> getInPort() {
        return inPort;
    }

    public void setOutPort(Port<T> outPort) {
        this.outPort = outPort;
    }
}
