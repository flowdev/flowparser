package org.flowdev.flowparser.cook;

import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.MainData;
import org.flowdev.flowparser.ParseToRawFlowFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.flowdev.flowparser.ParseToRawFlowFileTest.deleteFile;
import static org.flowdev.flowparser.TestUtils.readFile;
import static org.flowdev.flowparser.TestUtils.writeFile;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class CookFlowFileTest {
    private static final String FLOW_DIR = "./src/test/flow/flowparser/";
    private static final String RESULT_DIR = "./src/test/result/flowparser/cook/";

    private final String fileName;
    private final ParseToRawFlowFile rawParser;
    private final CookFlowFile cookParser;

    public CookFlowFileTest(String fileName) {
        this.fileName = fileName;
        rawParser = new ParseToRawFlowFile();
        cookParser = new CookFlowFile();
        rawParser.setOutPort(cookParser.getInPort());
    }

    @Parameterized.Parameters
    public static Collection<?> checkPorts() {
        return Arrays.asList(new Object[][]{ //
                {"mini"}, //
                {"connections"} //
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
        MainData data = new MainData();
        data.fileContent = flowFileContent;
        data.fileName = fileName;

        cookParser.setOutPort(d -> { /* nothing to do! */ });
        rawParser.getInPort().send(data);
        return PrettyPrinter.prettyPrint(data.flowFile);
    }
}
