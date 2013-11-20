package org.flowdev.flowparser;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.flowdev.flowparser.output.OutputAllFormatsConfig;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static java.util.Arrays.asList;

public class Main {
    private static OptionParser optParser;
    private static IMainFlow mainFlow = new MainFlow();

    /**
     * @param args command line arguments.
     * @throws IOException if something terrible happens.
     */
    public static void main(String[] args) throws IOException {
        optParser = new OptionParser();
        optParser.posixlyCorrect(true);
        try {
            OptionSpec<Void> help = optParser.acceptsAll(
                    asList("h", "?", "help"), "show this help page").forHelp();
            OptionSpec<String> outFormats = optParser
                    .acceptsAll(asList("f", "format"), "output formats")
                    .withRequiredArg().describedAs("graphviz, java6")
                    .withValuesSeparatedBy(",").defaultsTo("graphviz");
            OptionSet options = optParser.parse(args);

            if (options.has(help)) {
                help();
            }
            List<String> inNames = options.nonOptionArguments();
            if (inNames.isEmpty()) {
                fatal("Not enough flow file arguments!");
            }

            List<String> formats = convertFormats(outFormats.values(options));
            compileFlows(inNames, formats);
        } catch (OptionException oe) {
            fatal(oe.getLocalizedMessage());
        }
    }

    static void setMainFlow(IMainFlow flow) {
        mainFlow = flow;
    }

    private static void compileFlows(List<String> inNames,
                                     List<String> formats) {
        MainConfig mainConfig = new MainConfig();
        mainConfig.outputAllFormats = new OutputAllFormatsConfig();
        mainConfig.outputAllFormats.formats = formats;
        mainFlow.getConfigPort().send(mainConfig);

        for (String inName : inNames) {
            MainData mainData = new MainData();
            mainData.fileName = inName;
            mainFlow.getInPort().send(mainData);
        }
    }

    private static List<String> convertFormats(List<String> formats) {
        if (formats.isEmpty()) {
            fatal("No output format selected!");
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
