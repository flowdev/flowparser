package org.flowdev.flowparser.output;

import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.op.Filter;

public class CreateOutputFileName<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
        public Getter<T, String> getFormat;
        public Getter<T, String> getFileName;
        public Setter<String, T, T> setFileName;
    }

    private final Params<T> params;

    public CreateOutputFileName(Params<T> params) {
        this.params = params;
    }

    @Override
    protected void filter(T data) {
        String format = params.getFormat.get(data);
        String fileName = params.getFileName.get(data);
        fileName = correctFileName(fileName, format);
        params.setFileName.set(data, fileName);
        outPort.send(data);
    }

    private String correctFileName(String fileName, String format) {
        fileName = deleteExtension(fileName);
        return fileName + "." + format;
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
