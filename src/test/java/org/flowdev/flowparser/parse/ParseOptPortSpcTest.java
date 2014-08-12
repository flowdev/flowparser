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
public class ParseOptPortSpcTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", Void.TYPE), //
                makeTestData("no match 1", "p.1", Void.TYPE), //
                makeTestData("no match 3", "pt. ", Void.TYPE), //
                makeTestData("simple 1", "p ", createPort("p", false, 0)), //
                makeTestData("simple 2", "pt.0\t", createPort("pt", true, 0)), //
                makeTestData("simple 3", "looooongPortName  \t   ", createPort("looooongPortName", false, 0)), //
                makeTestData("simple 4", "port.123 \t ", createPort("port", true, 123))  //
        );
    }

    private static PortPair createPort(String name, boolean hasIndex, int index) {
        return new PortPair().inPort(name).hasInPortIndex(hasIndex).inPortIndex(index);
    }

    public ParseOptPortSpcTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseOptPortSpc<>(params);
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
