package org.flowdev.flowparser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.flowdev.flowparser.TestUtils.readResource;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PluginMainTest {
    private final String fileName;

    public PluginMainTest(String fileName) {
        this.fileName = fileName;
    }

    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        List<Object[]> testDatas = new ArrayList<>(2);
        for (String file : AllTest.FLOWS) {
            testDatas.add(new String[]{file});
        }
        return testDatas;
    }

    @Test
    public void testMiniFlow() throws Exception {
        String expectedResult = readResource(AllTest.RESULT_RESOURCE_DIR + fileName + ".adoc" + ".expected");
        String testFlowContent = readResource(AllTest.FLOW_RESOURCE_DIR + fileName + AllTest.FLOW_EXT);

        String actualResult = PluginMain.compileFlowToAdoc(testFlowContent, true);

        assertEquals("PluginMainTest failed for file '" + fileName + "' and format 'adoc'.", expectedResult, actualResult);
    }
}