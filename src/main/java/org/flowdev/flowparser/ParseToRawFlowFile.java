package org.flowdev.flowparser;

import mouse.runtime.Source;
import mouse.runtime.SourceString;
import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.rawdata.RawFlowFile;

/**
 * This operation parses a flow file from a string and an optional file name to
 * a raw flow file object.
 */
public class ParseToRawFlowFile<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
        public Getter<T, String> getFileName;
        public Getter<T, String> getFileContent;
        public Setter<RawFlowFile, T, T> setFlowFile;
    }

    private final Params<T> params;

    public ParseToRawFlowFile(Params<T> params) {
        this.params = params;
    }

    protected void filter(T data) {
        String fileContent = params.getFileContent.get(data);

        FlowParser parser = new FlowParser();
        Source src = new SourceString(fileContent);
        boolean ok = parser.parse(src);

        if (ok) {
            FlowParserSemantics sem = parser.semantics();
            params.setFlowFile.set(data, sem.getFlowFile());
        }

        outPort.send(data);
    }
}
