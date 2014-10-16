package org.flowdev.flowparser.output;

import org.flowdev.base.Port;
import org.flowdev.base.op.BaseOp;
import org.flowdev.flowparser.MainData;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterate over all requested formats and output them all.
 */
public class OutputAllFormats extends BaseOp<OutputAllFormatsConfig> {
    public static final String[] ALLOWED_FORMATS = {"adoc", "java", "wiki"};
    protected List<Port<MainData>> formatPorts = new ArrayList<>(ALLOWED_FORMATS.length);
    protected Port<MainData> inPort = (data) -> outputFormats(data);


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

    /**
     * This method is intentionally public static so it can be used by the main class.
     *
     * @param format the format to look for in the ALLOWED_FORMATS array.
     * @return the index of the given format in the ALLOWED_FORMATS array.
     */
    public static int formatIndex(String format) {
        for (int i = 0; i < ALLOWED_FORMATS.length; i++) {
            if (ALLOWED_FORMATS[i].equals(format)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method is intentionally public static so it can be used by the main class.
     *
     * @return the allowed output formats as a nicely formatted string.
     */
    public static String allowedFormats() {
        return String.join(", ", ALLOWED_FORMATS);
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

}
