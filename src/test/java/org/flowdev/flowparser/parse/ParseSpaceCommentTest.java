package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseSpaceCommentTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", ""), //
                makeTestData("no match", "baaa", ""), //
                makeTestData("simple 1", " i", " "), //
                makeTestData("simple 2", "\t0", "\t"), //
                makeTestData("simple 3", " /* bla */ _t", " /* bla */ "), //
                makeTestData("simple 4", " // comment! \n lilalo", " // comment! \n "), //
                makeTestData("complex", " /* bla\n */ \t // com!\n \t \r\n/** blu */ _t",
                        " /* bla\n */ \t // com!\n \t \r\n/** blu */ ")  //
        );
    }

    public ParseSpaceCommentTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseSpaceComment<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals(expectedValue, actualValue);
    }
}
