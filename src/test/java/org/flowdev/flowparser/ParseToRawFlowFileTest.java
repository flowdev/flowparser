package org.flowdev.flowparser;

import org.flowdev.base.Getter;
import org.flowdev.base.Port;
import org.flowdev.base.Setter;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.rawdata.RawFlowFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import static org.flowdev.flowparser.TestUtils.readFile;
import static org.flowdev.flowparser.TestUtils.writeFile;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ParseToRawFlowFileTest {
    public static final String FLOW_DIR = "./src/test/flow/flowparser/";
    private static final String RESULT_DIR = "./src/test/result/flowparser/";

    private ParseToRawFlowFile parser;
    private String fileName;

    public ParseToRawFlowFileTest(String fileName) {
        this.fileName = fileName;
        parser = new ParseToRawFlowFile();
    }

    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
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

        parser.setOutPort(d -> { /* nothing to do! */ });
        parser.getInPort().send(data);
        return PrettyPrinter.prettyPrint(data.rawFlowFile);
    }

    public static void deleteFile(String path) throws IOException {
        Files.deleteIfExists(getPath(path));
    }

    private static Path getPath(String path) {
        return FileSystems.getDefault().getPath(path);
    }
}
