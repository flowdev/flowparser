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
public class ParseBigIdentTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "baaa", null), //
                makeTestData("no match 2", "A", null), //
                makeTestData("simple", "Ab", "Ab"), //
                makeTestData("simple 2", "A0", "A0"), //
                makeTestData("simple 3", "Ab_cd", "Ab"), //
                makeTestData("simple 4", "AbcDef", "AbcDef"), //
                makeTestData("simple 5", "Abc123Def", "Abc123Def")  //
        );
    }

    public ParseBigIdentTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseBigIdent<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals(expectedValue, actualValue);
    }
}
