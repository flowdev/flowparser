package org.flowdev.flowparser.output;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.MainData;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class FillTemplate extends FilterOp<MainData, FillTemplate.FillTemplateConfig> {
    private static final String TEMPLATE_DIR = FillTemplate.class.getPackage().getName().replace('.', '/') + "/";

    @Override
    protected void filter(MainData data) {
        MustacheFactory mf = new DefaultMustacheFactory(
                TEMPLATE_DIR + data.format());
        Mustache standardTpl = mf.compile("template.mustache");
        StringBuilder fileContent = new StringBuilder(8192);
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("horizontal", getVolatileConfig().horizontal());

        for (Flow flow : data.flowFile().flows()) {
            tplData.put("flow", flow);
            StringWriter sw = new StringWriter();
            standardTpl.execute(sw, tplData);
            sw.flush();
            fileContent.append(sw.toString());
        }

        data.outputContent(fileContent.toString());
        outPort.send(data);
    }

    public static class FillTemplateConfig {
        private boolean horizontal;

        public boolean horizontal() {
            return horizontal;
        }

        public FillTemplateConfig horizontal(boolean horizontal) {
            this.horizontal = horizontal;
            return this;
        }
    }
}
