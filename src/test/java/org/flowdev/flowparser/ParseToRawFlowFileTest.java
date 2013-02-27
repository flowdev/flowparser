package org.flowdev.flowparser;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.rawdata.RawFlowFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ParseToRawFlowFileTest {
    public static final String UTF8 = "UTF8";
    public static final String FLOW_DIR = "./src/test/flow/flowparser/";
    private static final String RESULT_DIR = "./src/test/result/flowparser/";
    private static final ParseToRawFlowFile.Params<TestData> PARAMS = new ParseToRawFlowFile.Params<>();
    static {
	PARAMS.getFileName = new Getter<TestData, String>() {

	    @Override
	    public String get(TestData data) {
		return data.fileName;
	    }
	};
	PARAMS.getFileContent = new Getter<TestData, String>() {

	    @Override
	    public String get(TestData data) {
		return data.fileContent;
	    }
	};
	PARAMS.setFlowFile = new Setter<TestData, RawFlowFile>() {

	    @Override
	    public void set(TestData data, RawFlowFile subdata) {
		data.result = subdata;
	    }
	};
    }

    private ParseToRawFlowFile<TestData> parser;
    private String fileName;

    public ParseToRawFlowFileTest(String fileName) {
	this.fileName = fileName;
	parser = new ParseToRawFlowFile<>(PARAMS);
    }

    @Parameterized.Parameters
    public static Collection<?> checkPorts() {
	return Arrays.asList(new Object[][] { //
		{ "mini" }, //
			{ "connections" }, //
			{ "config" }, //
			{ "create" }, //
			{ "getter" }, //
			{ "setter" } //
		});
    }

    @Test
    public void testParser() throws IOException {
	String actualFile = RESULT_DIR + fileName + ".actual";
	deleteFile(actualFile);
	String flowFileContent = readFile(FLOW_DIR + fileName + ".flow");
	String expectedResult = readFile(RESULT_DIR + fileName + ".expected");
	String actualResult = parseFlow(flowFileContent, fileName + ".flow");
	if (!expectedResult.equals(actualResult)) {
	    writeFile(actualFile, actualResult);
	    fail("ParserTest failed for file '" + fileName
		    + "'. You can find the offending parser output at: "
		    + actualFile);
	}

    }

    private String parseFlow(String flowFileContent, String fileName) {
	TestData data = new TestData();
	data.fileContent = flowFileContent;
	data.fileName = fileName;

	parser.setOut(new Port<TestData>() {

	    @Override
	    public void send(TestData data) {
		// nothing to do!
	    }
	});
	parser.getIn().send(data);
	return PrettyPrinter.prettyPrint(data.result);
    }

    public static String readFile(String path) throws IOException {
	byte[] bytes = Files.readAllBytes(getPath(path));
	return new String(bytes, UTF8);
    }

    public static void writeFile(String path, String content)
	    throws IOException {
	Files.write(getPath(path), content.getBytes(UTF8));
    }

    public static void deleteFile(String path) throws IOException {
	Files.deleteIfExists(getPath(path));
    }

    private static Path getPath(String path) {
	return FileSystems.getDefault().getPath(path);
    }

    private static class TestData {
	public String fileName;
	public String fileContent;
	public RawFlowFile result;
    }
}
