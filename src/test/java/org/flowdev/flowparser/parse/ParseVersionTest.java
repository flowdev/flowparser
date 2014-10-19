package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.Version;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseVersionTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "vers", null), //
                makeTestData("no match 2", "version", null), //
                makeTestData("no match 3", "version .1", null), //
                makeTestData("no match 4", "version \t 2.  ", null), //
                makeTestData("no match 5", "version \n 2.0  ", null), //
                makeTestData("simple 1", "version 0.1", new Version().political(0).major(1)), //
                makeTestData("simple 2", "\t version \t 1.234\n ", new Version().political(1).major(234)), //
                makeTestData("simple 3", " /* bla */version \t 1.234 \n _t", new Version().political(1).major(234)), //
                makeTestData("simple 4", " // comment! \n version \t 1.234 \n ", new Version().political(1).major(234)), //
                makeTestData("complex", " /* bla\n */ \t // com!\n \t version \t 1.234 \r\n/** blu */ _t",
                        new Version().political(1).major(234))  //
        );
    }

    public ParseVersionTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseVersion(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        Version expected = (Version) expectedValue;
        Version actual = (Version) actualValue;
        assertEquals("Political version doesn't match.", expected.political(), actual.political());
        assertEquals("Major version doesn't match.", expected.major(), actual.major());
    }
}
