package org.flowdev.flowparser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.flowdev.flowparser.TestUtils.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AllTest {
    private static final String WORK_DIR = System.getProperty("java.io.tmpdir", "./");
    private static final String FLOW_RESOURCE_DIR = "flow/flowparser/";
    private static final String RESULT_RESOURCE_DIR = "result/flowparser/all/";
    private static final String FLOW_EXT = ".flow";
    private static final String FORMAT = "wiki";
    private static final String FORMAT_EXT = "." + FORMAT;
    private static final String EXPECTED_FORMAT_EXT = FORMAT_EXT + ".expected";

    private final String fileName;

    public AllTest(String fileName) {
        this.fileName = fileName;
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
        String workFlowFile = WORK_DIR + fileName + FLOW_EXT;
        String actualFile = WORK_DIR + fileName + FORMAT_EXT;
        String expectedResult = readResource(RESULT_RESOURCE_DIR + fileName + EXPECTED_FORMAT_EXT);
        String testFlowContent = readResource(FLOW_RESOURCE_DIR + fileName + FLOW_EXT);

        deleteFile(actualFile);
        deleteFile(workFlowFile);
        writeFile(workFlowFile, testFlowContent);

        Main.resetMainFlow();
        Main.main("-f", FORMAT, workFlowFile);
        String actualResult = readFile(actualFile);

        deleteFile(actualFile);
        deleteFile(workFlowFile);
        assertEquals("AllTest failed for file '" + fileName + "'.", expectedResult, actualResult);
    }

}
