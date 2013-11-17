package org.flowdev.flowparser.cook;

import static org.flowdev.flowparser.ParseToRawFlowFileTest.deleteFile;
import static org.flowdev.flowparser.ParseToRawFlowFileTest.readFile;
import static org.flowdev.flowparser.ParseToRawFlowFileTest.writeFile;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.ParseToRawFlowFile;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.rawdata.RawFlowFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CookFlowFileTest {
    private static final String FLOW_DIR = "./src/test/flow/flowparser/";
    private static final String RESULT_DIR = "./src/test/result/flowparser/cook/";
    private static final ParseToRawFlowFile.Params<TestData> RAW_PARAMS = new ParseToRawFlowFile.Params<>();
    private static final CookFlowFile.Params<TestData> COOK_PARAMS = new CookFlowFile.Params<>();
    static {
	RAW_PARAMS.getFileName = new Getter<TestData, String>() {
	    @Override
	    public String get(TestData data) {
		return data.fileName;
	    }
	};
	RAW_PARAMS.getFileContent = new Getter<TestData, String>() {
	    @Override
	    public String get(TestData data) {
		return data.fileContent;
	    }
	};
	RAW_PARAMS.setFlowFile = new Setter<RawFlowFile, TestData, TestData>() {
	    @Override
	    public TestData set(TestData data, RawFlowFile subdata) {
		data.rawFlowFile = subdata;
		return data;
	    }
	};
    }

    static {
	COOK_PARAMS.getFileName = new Getter<TestData, String>() {
	    @Override
	    public String get(TestData data) {
		return data.fileName;
	    }
	};
	COOK_PARAMS.getRawFlowFile = new Getter<TestData, RawFlowFile>() {
	    @Override
	    public RawFlowFile get(TestData data) {
		return data.rawFlowFile;
	    }
	};
	COOK_PARAMS.setCookedFlowFile = new Setter<FlowFile, TestData, TestData>() {
	    @Override
	    public TestData set(TestData data, FlowFile subdata) {
		data.result = subdata;
		return data;
	    }
	};
    }

    private String fileName;
    private ParseToRawFlowFile<TestData> rawParser;
    private CookFlowFile<TestData> cookParser;

    public CookFlowFileTest(String fileName) {
	this.fileName = fileName;
	rawParser = new ParseToRawFlowFile<>(RAW_PARAMS);
	cookParser = new CookFlowFile<>(COOK_PARAMS);
	rawParser.setOut(cookParser.getIn());
    }

    @Parameterized.Parameters
    public static Collection<?> checkPorts() {
	return Arrays.asList(new Object[][] { //
		{ "mini" }, //
		{ "connections" } //
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

	cookParser.setOut(new Port<TestData>() {
	    @Override
	    public void send(TestData data) {
		// nothing to do!
	    }
	});
	rawParser.getIn().send(data);
	return PrettyPrinter.prettyPrint(data.result);
    }

    private static class TestData {
	public String fileName;
	public String fileContent;
	public RawFlowFile rawFlowFile;
	public FlowFile result;
    }
}
