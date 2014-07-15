package org.flowdev.flowparser.output;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.MainData;

public class CreateOutputFileName extends Filter<MainData, NoConfig> {
    @Override
    protected void filter(MainData data) {
        String fileName = data.fileName;
        fileName = deleteExtension(fileName);
        data.fileName = fileName + "." + data.format;
        outPort.send(data);
    }

    private String deleteExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            return fileName.substring(0, i);
        } else {
            return fileName;
        }
    }
}
