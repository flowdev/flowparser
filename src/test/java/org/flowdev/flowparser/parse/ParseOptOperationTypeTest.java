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
public class ParseOptOperationTypeTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", Void.TYPE), //
                makeTestData("no match 1", "blu", Void.TYPE), //
                makeTestData("no match 2", "B", Void.TYPE), //
                makeTestData("no match 3", " ", Void.TYPE), //
                makeTestData("simple 1", "Bla", "Bla"), //
                makeTestData("simple 2", "Blu  ", "Blu"), //
                makeTestData("simple 3", "Blu  \t  \t ", "Blu")  //
        );
    }

    public ParseOptOperationTypeTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseOptOperationType<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Operation type name doesn't match.", expectedValue.toString(), actualValue.toString());
    }
}
