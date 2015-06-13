package org.flowdev.flowparser;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.flowdev.flowparser.CoreFlow.CoreFlowConfig;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.output.FillTemplate;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.data.SourceData;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.flowdev.flowparser.output.OutputAllFormats.OutputAllFormatsConfig;
import static org.flowdev.flowparser.util.FormatUtil.formatIndex;
import static org.flowdev.flowparser.util.FormatUtil.formatsAsString;

public class Main {
    private static OptionParser optParser;
    private static IMainFlow mainFlow;

    static {
        resetMainFlow();
    }

    /**
     * @param args command line arguments.
     * @throws IOException if something terrible happens.
     */
    public static void main(String... args) throws IOException {
        boolean horizontal = false;
        optParser = new OptionParser();
        optParser.posixlyCorrect(true);
        try {
            OptionSpec<Void> help = optParser.acceptsAll(
                    asList("h", "?", "help"), "show this help page").forHelp();
            OptionSpec<String> outFormats = optParser
                    .acceptsAll(asList("f", "format"), "output formats")
                    .withRequiredArg().describedAs(formatsAsString())
                    .withValuesSeparatedBy(",").defaultsTo("adoc");
            optParser.accepts("horizontal", "format flows horizontaly");
            OptionSet options = optParser.parse(args);

            if (options.has(help)) {
                help();
            }
            if (options.has("horizontal")) {
                horizontal = true;
            }
            List<String> inNames = options.nonOptionArguments();
            if (inNames.isEmpty()) {
                fatal("Not enough flow file arguments!");
            }

            List<String> formats = convertFormats(outFormats.values(options));
            compileFlows(inNames, formats, horizontal);
        } catch (OptionException oe) {
            fatal(oe.getLocalizedMessage());
        }
    }

    static void setMainFlow(IMainFlow flow) {
        mainFlow = flow;
    }

    static void resetMainFlow() {
        mainFlow = new MainFlow();
        mainFlow.setErrorPort(err -> {
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
    }

    private static void compileFlows(List<String> inNames, List<String> formats, boolean horizontal) {
        CoreFlowConfig mainFlowConfig = new CoreFlowConfig().outputAllFormats(new OutputAllFormatsConfig().formats(formats)) //
                .fillTemplate(new FillTemplate.FillTemplateConfig().horizontal(horizontal));
        mainFlow.getConfigPort().send(mainFlowConfig);

        for (String inName : inNames) {
            MainData mainData = new MainData().parserData(new ParserData().source(new SourceData().name(inName)));
            System.out.println("Compiling file: " + inName);
            mainFlow.getInPort().send(mainData);
        }
    }

    private static List<String> convertFormats(List<String> formats) {
        if (formats.isEmpty()) {
            fatal("No output format selected!");
        }
        for (String format : formats) {
            if (formatIndex(format) < 0) {
                fatal("Unknown output format: " + format);
            }
        }

        System.out.println("Formats: " + formats.toString());

        return formats;
    }

    private static void help() {
        abort("flow parser usage:", System.out, 0);
    }

    private static void fatal(String message) {
        abort(message, System.err, 1);
    }

    private static void abort(String message, PrintStream stream, int exitValue) {
        try {
            stream.println(message);
            stream.println();
            optParser.printHelpOn(stream);
            stream.flush();
        } catch (Exception e) {
            // Ignored because we don't want to hide real error!
        }
        System.exit(exitValue);
    }

}
