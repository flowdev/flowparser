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
public class ParseArrowTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "-", null), //
                makeTestData("no match 3", " /* \n */ \t [Bla]>", null), //
                makeTestData("simple 1", "[Bla]->", "Bla"), //
                makeTestData("simple 2", "->", Void.TYPE), //
                makeTestData("simple 3", "\n \t /* Blu */ [ \t Bla \t ]->  \t   ", "Bla"), //
                makeTestData("simple 4", " \r\n // blu \n \t -> \r\n \t", Void.TYPE)  //
        );
    }

    public ParseArrowTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseArrow(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Type name doesn't match.", expectedValue.toString(), actualValue.toString());
    }
}
