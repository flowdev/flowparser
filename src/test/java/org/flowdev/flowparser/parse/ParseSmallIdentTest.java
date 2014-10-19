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
public class ParseSmallIdentTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "ABCD", null), //
                makeTestData("no match 2", "123", null), //
                makeTestData("simple", "aB", "aB"), //
                makeTestData("simple 2", "a0", "a0"), //
                makeTestData("simple 3", "aB_CD", "aB"), //
                makeTestData("simple 4", "aBCdEF", "aBCdEF"), //
                makeTestData("simple 5", "aBC123dEF", "aBC123dEF")  //
        );
    }

    public ParseSmallIdentTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseSmallIdent(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals(expectedValue, actualValue);
    }
}
