package org.flowdev.flowparser.mustache;

import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.op.Transform;

public class OutputFlow extends
	Transform<OutFlowData, OutFileData, EmptyConfig> {

    @Override
    protected OutFileData transform(OutFlowData data) {
	return null;
    }

}
