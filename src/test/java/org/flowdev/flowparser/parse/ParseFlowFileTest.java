package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class ParseFlowFileTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Collection<?> testDatas = asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "vers", null), //
                makeTestData("no match 2", "version \n 2.0  ", null), //
                makeTestData("no match 3", " /* bla */version \t 1.234 \n _t", null), //
                makeTestData("simple 1", "version 0.1", new FlowFile().fileName("simple 1").version(new Version().political(0).major(1))), //
                makeTestData("simple 2", "\t version \t 1.234\n ", new FlowFile().fileName("simple 2").version(new Version().political(1).major(234))), //
                makeTestData("simple 3", " /* bla */version \t 1.234 \n ", new FlowFile().fileName("simple 3").version(new Version().political(1).major(234))), //
                makeTestData("simple 4", " // comment! \n version \t 1.234 \n ", new FlowFile().fileName("simple 4").version(new Version().political(1).major(234))), //
                makeTestData("complex", " /* bla\n */ \t // com!\n \t version \t 1.234 \r\n/** blu */ ",
                        new FlowFile().fileName("complex").version(new Version().political(1).major(234)))  //
        );
        return testDatas;
    }

    public ParseFlowFileTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseFlowFile<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        if (expectedValue == null) {
            assertNull("Actual value should be null.", actualValue);
            return;
        }
        assertEquals("Expected and actual value don't have the same class.", expectedValue.getClass(), actualValue.getClass());

        FlowFile expected = (FlowFile) expectedValue;
        FlowFile actual = (FlowFile) actualValue;
        assertEquals("File name doesn't match.", expected.fileName(), actual.fileName());
        assertEquals("Political version doesn't match.", expected.version().political(), actual.version().political());
        assertEquals("Major version doesn't match.", expected.version().major(), actual.version().major());
    }
}
