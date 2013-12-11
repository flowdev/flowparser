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
public class ParseToRawFlowFile extends Filter<MainData, EmptyConfig> {
    protected void filter(MainData data) {
        FlowParser parser = new FlowParser();
        Source src = new SourceString(data.fileContent);
        boolean ok = parser.parse(src);

        if (ok) {
            FlowParserSemantics sem = parser.semantics();
            data.rawFlowFile = sem.getFlowFile();
        }

        outPort.send(data);
    }
}
