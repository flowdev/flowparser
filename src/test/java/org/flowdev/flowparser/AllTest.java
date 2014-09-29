package org.flowdev.flowparser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.flowdev.flowparser.TestUtils.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AllTest {
    private static final String WORK_DIR = System.getProperty("java.io.tmpdir", ".") + "/";
    private static final String FLOW_RESOURCE_DIR = "flow/flowparser/";
    private static final String RESULT_RESOURCE_DIR = "result/flowparser/all/";
    private static final String FLOW_EXT = ".flow";
    //    private static final String[] FORMATS = { "adoc", "wiki", "java" };
    private static final String[] FORMATS = {"adoc", "wiki"};
    private static final String[] FLOWS = {"mini", "connections"};

    private final String fileName;
    private final String format;

    public AllTest(String fileName, String format) {
        this.fileName = fileName;
        this.format = format;
    }

    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        List<Object[]> testDatas = new ArrayList<>(3 * 2);
        for (String fmt : FORMATS) {
            for (String file : FLOWS) {
                testDatas.add(new String[]{file, fmt});
            }
        }
        return testDatas;
    }

    @Test
    public void testParser() throws IOException {
        String workFlowFile = WORK_DIR + fileName + FLOW_EXT;
        String actualFile = WORK_DIR + fileName + "." + format;
        String expectedResult = readResource(RESULT_RESOURCE_DIR + fileName + "." + format + ".expected");
        String testFlowContent = readResource(FLOW_RESOURCE_DIR + fileName + FLOW_EXT);

        deleteFile(actualFile);
        deleteFile(workFlowFile);
        writeFile(workFlowFile, testFlowContent);

        Main.resetMainFlow();
        Main.main("-f", format, workFlowFile);
        String actualResult = readFile(actualFile);

        deleteFile(actualFile);
        deleteFile(workFlowFile);
        assertEquals("AllTest failed for file '" + fileName + "' and format '" + format + "'.", expectedResult, actualResult);
    }

}
