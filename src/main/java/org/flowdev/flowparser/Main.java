package org.flowdev.flowparser;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.flowdev.flowparser.mustache.OutputFlowFileConfig;

public class Main {

    private final static String fSep = File.separator;

    private static OptionParser optParser;
    private static IMainFlow mainFlow = new MainFlow();

    /**
     * @param args
     *            command line arguments.
     * @throws IOException
     *             if something terrible happens.
     */
    public static void main(String[] args) throws IOException {
	optParser = new OptionParser();
	optParser.posixlyCorrect(true);
	try {
	    OptionSpec<Void> help = optParser.acceptsAll(
		    asList("h", "?", "help"), "show this help page").forHelp();
	    OptionSpec<String> outFormats = optParser
		    .acceptsAll(asList("f", "format"), "output formats")
		    .withRequiredArg().describedAs("java,json")
		    .withValuesSeparatedBy(",").defaultsTo("json");
	    OptionSpec<String> outRoots = optParser
		    .acceptsAll(
			    asList("r", "root"),
			    "root directories for output formats (must be the same number and order as the formats)")
		    .withRequiredArg().withValuesSeparatedBy(",");
	    OptionSet options = optParser.parse(args);

	    if (options.has(help)) {
		help();
	    }
	    List<String> inNames = options.nonOptionArguments();
	    if (inNames.isEmpty()) {
		fatal("Not enough flow file arguments!");
	    }

	    Map<String, String> formatMap = convertFormats(
		    outFormats.values(options), outRoots.values(options),
		    inNames);
	    System.out.println("FormatMap: " + formatMap.toString());

	    compileFlows(inNames, formatMap);
	} catch (OptionException oe) {
	    fatal(oe.getLocalizedMessage());
	}
    }

    static void setMainFlow(IMainFlow flow) {
	mainFlow = flow;
    }

    private static void compileFlows(List<String> inNames,
	    Map<String, String> formatMap) {
	MainConfig mainConfig = new MainConfig();
	mainConfig.outputFlowFile = new OutputFlowFileConfig();
	mainConfig.outputFlowFile.roots = formatMap;
	mainFlow.getConfig().send(mainConfig);

	for (String inName : inNames) {
	    MainData mainData = new MainData();
	    mainData.fileName = inName;
	    mainFlow.getIn().send(mainData);
	}
    }

    private static Map<String, String> convertFormats(List<String> formats,
	    List<String> roots, List<String> inNames) {
	if (formats.isEmpty()) {
	    fatal("No output format selected!");
	}
	System.out.println("Formats: " + formats.toString());
	if (!roots.isEmpty() && formats.size() != roots.size()) {
	    fatal("The number of output root directories (" + join(", ", roots)
		    + ") must match the number of formats ("
		    + join(", ", formats) + ")");
	}

	Map<String, String> ret = new HashMap<>(formats.size() * 2);
	List<String> myRoots = getOutputRoots(formats, roots, inNames);

	for (int i = 0; i < formats.size(); i++) {
	    ret.put(formats.get(i), myRoots.get(i));
	}
	return ret;
    }

    private static List<String> getOutputRoots(List<String> formats,
	    List<String> roots, List<String> inNames) {
	if (roots.isEmpty()) {
	    List<String> myRoots = new ArrayList<>(formats.size());
	    for (String fmt : formats) {
		myRoots.add(getOutputRoot(fmt, inNames));
	    }
	    return myRoots;
	} else {
	    return roots;
	}
    }

    static String getOutputRoot(String format, List<String> inNames) {
	for (String inName : inNames) {
	    String inPath = getRealPath(inName);
	    String searchPattern = fSep + "src" + fSep + "([^" + fSep + "]+)"
		    + fSep + "flow" + fSep + ".*";
	    String replaceString = fSep + "src" + fSep + "$1" + fSep + format
		    + fSep;
	    if (inPath.matches(".*" + searchPattern)) {
		String outRoot = inPath.replaceFirst(searchPattern,
			replaceString);
		return outRoot;
	    } else {
		System.out.println("No Match: " + inPath + " ; "
			+ searchPattern);
	    }
	}
	fatal("Unable to create default output root from input flows:\n  "
		+ join("\n  ", inNames));
	return null;
    }

    private static String getRealPath(String path) {
	try {
	    return Paths.get(path).toRealPath().toString();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private static String join(String string, List<String> strings) {
	StringBuilder buf = new StringBuilder(128);
	for (String str : strings) {
	    if (buf.length() > 0) {
		buf.append(", ");
	    }
	    buf.append(str);
	}
	return buf.toString();
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
	} catch (Exception e) {
	    // Ignored because we don't want to hide real error!
	}
	System.exit(exitValue);
    }

}
