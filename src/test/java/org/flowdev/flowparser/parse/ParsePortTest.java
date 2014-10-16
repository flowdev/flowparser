package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
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
public class ParsePortTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", ".1", null), //
                makeTestData("half match 1", "pt.", newPort(0, "pt")), //
                makeTestData("half match 2", "pt_1", newPort(0, "pt")), //
                makeTestData("simple 1", "p", newPort(0, "p")), //
                makeTestData("simple 2", "pt.0", newPort(0, "pt", 0)), //
                makeTestData("simple 3", "looooongPortName", newPort(0, "looooongPortName")), //
                makeTestData("simple 4", "port.123", newPort(0, "port", 123))  //
        );
    }

    public ParsePortTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParsePort<>(params);
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
