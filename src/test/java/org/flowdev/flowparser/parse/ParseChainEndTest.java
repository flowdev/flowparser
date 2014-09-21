package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseChainEndTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Connection connMin = new Connection();
        Connection connNoPort = new Connection().dataType("Bla").showDataType(true);
        Connection connNoType = new Connection().toPort(newPort("outX", 3));
        Connection connMax = new Connection().dataType("Blu").showDataType(true).toPort(newPort("outX", 7));
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "-", null), //
                makeTestData("no match 3", " /* \n */ \t [Bla]>", null), //
                makeTestData("simple 1", "->", connMin), //
                makeTestData("simple 2", " \t [Bla]-> ", connNoPort), //
                makeTestData("simple 3", " \r\n // blu \n \t -> \r\n outX.3", connNoType), //
                makeTestData("simple 4", "\n \t /* Bla */ [ \t Blu \t ]->  \t outX.7", connMax)  //
        );
    }

    public ParseChainEndTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseChainEnd<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Connection doesn't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
