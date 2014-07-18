package org.flowdev.flowparser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.flowdev.flowparser.TestUtils.*;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class AllTest {
    private static final String FLOW_DIR = "./src/test/flow/flowparser/";
    private static final String WORK_DIR = "./src/test/result/flowparser/all/";
    private static final String FLOW_EXT = ".flow";

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
        String actualFile = WORK_DIR + fileName + ".graphviz";
        String expectedResult = readFile(WORK_DIR + fileName + ".graphviz.expected");

        deleteFile(actualFile);
        deleteFile(workFlowFile);
        copyFile(FLOW_DIR + fileName + FLOW_EXT, workFlowFile);

        Main.resetMainFlow();
        Main.main("-f", "graphviz", workFlowFile);

        String actualResult = readFile(actualFile);
        if (expectedResult.equals(actualResult)) {
            deleteFile(actualFile);
            deleteFile(workFlowFile);
        } else {
            fail("AllTest failed for file '" + fileName + "'. You can find the offending output at: " + actualFile);
        }
    }

}
