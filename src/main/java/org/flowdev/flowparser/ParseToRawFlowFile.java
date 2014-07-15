package org.flowdev.flowparser;

import mouse.runtime.Source;
import mouse.runtime.SourceString;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;

/**
 * This operation parses a flow file from a string and an optional file name to
 * a raw flow file object.
 */
public class ParseToRawFlowFile extends Filter<MainData, NoConfig> {
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
