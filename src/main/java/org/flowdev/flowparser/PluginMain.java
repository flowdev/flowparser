package org.flowdev.flowparser;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Consume;
import org.flowdev.flowparser.CoreFlow.CoreFlowConfig;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.output.FillTemplate.FillTemplateConfig;
import org.flowdev.flowparser.output.OutputAllFormats;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.data.SourceData;

import static java.util.Arrays.asList;

/**
 * Main class for the AsciiDoctor plugin.
 */
public class PluginMain {

    /**
     * Compile a flowdev DSL flow into an Asciidoctor diagram.
     *
     * @param flow the flow in DSL form as a string.
     * @param horizontal true if the flow should be formatted horizontaly.
     * @return the flow compiled to an Asciidoctor graphviz diagram.
     */
    public static String compileFlowToAdoc(String flow, boolean horizontal) {
        CoreFlow coreFlow = new CoreFlow();
        coreFlow.setErrorPort(err -> {
            if (err == null) {
                String msg = "Unknown error occured. Exception is null.";
                System.err.println(msg);
                throw new RuntimeException(msg);
            } else {
                System.err.println("The flow parser caught a runtime error:");
                err.fillInStackTrace();
                err.printStackTrace();
                throw new RuntimeException(err);
            }
//            System.exit(1);
        });

        CoreFlowConfig mainFlowConfig = new CoreFlowConfig().outputAllFormats(new OutputAllFormats.OutputAllFormatsConfig().formats(asList("adoc"))) //
                .fillTemplate(new FillTemplateConfig().horizontal(horizontal));
        coreFlow.getConfigPort().send(mainFlowConfig);

        ResultRecipient resultRecipient = new ResultRecipient();
        coreFlow.setOutPort(resultRecipient.getInPort());

        MainData mainData = new MainData().parserData(new ParserData().source(new SourceData().name("<Asciidoctor plugin>").content(flow)));
        coreFlow.getInPort().send(mainData);

        return resultRecipient.result().outputContent();
    }

    public static class ResultRecipient extends Consume<MainData, NoConfig> {
        private MainData result;

        @Override
        protected void consume(MainData data) throws Exception {
            this.result = data;
        }

        public MainData result() {
            return result;
        }
    }
}
