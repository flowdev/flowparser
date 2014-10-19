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
public class ParseStatementEndTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null),
                makeTestData("no match 1", "baaa", null),
                makeTestData("no match 2", " /* bla\n */ \t // com!\n \t \r\n/** blu ; */ ", null),
                makeTestData("simple 1", ";", ";"),
                makeTestData("simple 2", "\t;0", "\t;"),
                makeTestData("simple 3", " /* bla */; _t", " /* bla */; "),
                makeTestData("simple 4", " // comment! \n ;lilalo", " // comment! \n ;"),
                makeTestData("complex", " /* bla\n */ \t; // com!\n \t \r\n/** blu */ _t",
                        " /* bla\n */ \t; // com!\n \t \r\n/** blu */ ")
        );
    }

    public ParseStatementEndTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseStatementEnd(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals(expectedValue, actualValue);
    }
}
