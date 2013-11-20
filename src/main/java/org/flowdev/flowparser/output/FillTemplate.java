package org.flowdev.flowparser.output;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.flowdev.base.Getter;
import org.flowdev.base.Setter;
import org.flowdev.base.data.EmptyConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.FlowFile;

import java.io.StringWriter;


public class FillTemplate<T> extends Filter<T, EmptyConfig> {
    public static class Params<T> {
        public Getter<T, String> getFormat;
        public Getter<T, FlowFile> getFlowFile;
        public Setter<String, T, T> setFileContent;
    }
    static final String TEMPLATE_DIR = FillTemplate.class.getPackage().getName().replace('.', '/') + "/";

    private final Params<T> params;

    public FillTemplate(Params<T> params) {
        this.params = params;
    }

    @Override
    protected T filter(T data) {
        String format = params.getFormat.get(data);
        FlowFile flowFile = params.getFlowFile.get(data);

        MustacheFactory mf = new DefaultMustacheFactory(
                TEMPLATE_DIR + format);
        Mustache mustache = mf.compile("template.mustache");
        StringWriter sw = new StringWriter();
        mustache.execute(sw, flowFile);
        String fileContent = sw.toString();

        params.setFileContent.set(data, fileContent);
        return data;
    }
}
