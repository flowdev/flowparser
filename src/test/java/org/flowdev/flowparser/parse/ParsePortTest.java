package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParsePortTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", ".1", null), //
                makeTestData("half match 1", "pt.", createPort("pt", false, 0)), //
                makeTestData("half match 2", "pt_1", createPort("pt", false, 0)), //
                makeTestData("simple 1", "p", createPort("p", false, 0)), //
                makeTestData("simple 2", "pt.0", createPort("pt", true, 0)), //
                makeTestData("simple 3", "looooongPortName", createPort("looooongPortName", false, 0)), //
                makeTestData("simple 4", "port.123", createPort("port", true, 123))  //
        );
    }

    private static PortPair createPort(String name, boolean hasIndex, int index) {
        return new PortPair().inPort(name).hasInPortIndex(hasIndex).inPortIndex(index);
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
        PortPair expected = (PortPair) expectedValue;
        PortPair actual = (PortPair) actualValue;
        assertEquals("Port name doesn't match.", expected.inPort(), actual.inPort());
        assertEquals("Port has index doesn't match.", expected.hasInPortIndex(), actual.hasInPortIndex());
        if (expected.hasInPortIndex()) {
            assertEquals("Port index doesn't match.", expected.inPortIndex(), actual.inPortIndex());
        }
    }
}
