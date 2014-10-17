package org.flowdev.flowparser.output;

import org.flowdev.base.Port;
import org.flowdev.base.op.BaseOp;
import org.flowdev.flowparser.MainData;

import java.util.ArrayList;
import java.util.List;

import static org.flowdev.flowparser.util.FormatUtil.formatCount;
import static org.flowdev.flowparser.util.FormatUtil.formatIndex;

/**
 * Iterate over all requested formats and output them all.
 */
public class OutputAllFormats extends BaseOp<OutputAllFormats.OutputAllFormatsConfig> {
    protected List<Port<MainData>> formatPorts = new ArrayList<>(formatCount());
    protected Port<MainData> inPort = this::outputFormats;


    protected void outputFormats(MainData data) {
        List<String> formats = getVolatileConfig().formats;
        for (String format : formats) {
            int idx = formatIndex(format);
            if (idx >= 0) {
                data.format(format);
                formatPorts.get(idx).send(data);
            } else {
                System.err.println("ERROR: Unable to ouput unknown format: " + format);
            }
        }
    }

    public Port<MainData> getInPort() {
        return inPort;
    }

    public void setFormatPort(int i, Port<MainData> subOutPort) {
        if (i == formatPorts.size()) {
            this.formatPorts.add(subOutPort);
        } else {
            this.formatPorts.set(i, subOutPort);
        }
    }

    public static class OutputAllFormatsConfig {
        public List<String> formats;

        public List<String> formats() {
            return this.formats;
        }

        public OutputAllFormatsConfig formats(final List<String> formats) {
            this.formats = formats;
            return this;
        }
    }
}
