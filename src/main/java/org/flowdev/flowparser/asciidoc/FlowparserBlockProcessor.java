package org.flowdev.flowparser.asciidoc;

import org.asciidoctor.ast.AbstractBlock;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowparserBlockProcessor extends BlockProcessor {
    private static Map<String, Object> configs = new HashMap<>();

    static {
        configs.put("contexts", Arrays.asList(":literal"));
        configs.put("content_model", ":raw");
    }

    public FlowparserBlockProcessor(String name, Map<String, Object> ignoredConfig) {
        super(name, configs);
    }

    @Override
    public Object process(AbstractBlock parent, Reader reader, Map<String, Object> attributes) {
        List<String> lines = reader.readLines();
        StringBuilder sb = new StringBuilder(4096);
        sb.append("flowdev BlockProcessor: ");
        for (String line : lines) {
            System.out.println(line);
            sb.append(line).append('\n');
        }
        return createBlock(parent, "paragraph", sb.toString(), attributes, new HashMap<>());
    }
}
