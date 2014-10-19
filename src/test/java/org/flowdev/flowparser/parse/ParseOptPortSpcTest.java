package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.flowparser.data.PortData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseOptPortSpcTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", Void.TYPE), //
                makeTestData("no match 1", "p.1", Void.TYPE), //
                makeTestData("no match 3", "pt. ", Void.TYPE), //
                makeTestData("simple 1", "p ", newPort(0, "p")), //
                makeTestData("simple 2", "pt.0\t", newPort(0, "pt", 0)), //
                makeTestData("simple 3", "looooongPortName  \t   ", newPort(0, "looooongPortName")), //
                makeTestData("simple 4", "port.123 \t ", newPort(0, "port", 123))  //
        );
    }

    public ParseOptPortSpcTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseOptPortSpc(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        PortData expected = (PortData) expectedValue;
        PortData actual = (PortData) actualValue;
        assertEquals("Port name doesn't match.", expected.name(), actual.name());
        assertEquals("Port has index doesn't match.", expected.hasIndex(), actual.hasIndex());
        if (expected.hasIndex()) {
            assertEquals("Port index doesn't match.", expected.index(), actual.index());
        }
    }
}
