package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseOperationNameParensTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "()", null), //
                makeTestData("no match 2", "bla", null), //
                makeTestData("no match 3", "Bla", null), //
                makeTestData("simple 1", "(Bla)", new Operation().name("bla").type("Bla")), //
                makeTestData("simple 2", "bla()", new Operation().name("bla")), //
                makeTestData("simple 3", "bla(Blu)", new Operation().name("bla").type("Blu")), //
                makeTestData("simple 4", "bla \t ( \t Blu \t ) \t ", new Operation().name("bla").type("Blu"))  //
        );
    }

    public ParseOperationNameParensTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseOperationNameParens<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        Operation expected = (Operation) expectedValue;
        Operation actual = (Operation) actualValue;
        assertEquals("Operation name doesn't match.", expected.name(), actual.name());
        assertEquals("Operation type doesn't match.", expected.type(), actual.type());
    }
}
