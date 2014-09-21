package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseFlowTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Flow flow = new Flow().name("Ab").operations(asList(
                new Operation().name("bla").type("Bla").ports(asList(new PortPair().isLast(true)))
        )).connections(Collections.<Connection>emptyList());

        return asList(
                makeTestData("empty", "", null),
                makeTestData("no match 1", "flo", null),
                makeTestData("no match 2", "flow", null),
                makeTestData("no match 3", "flow Ab", null),
                makeTestData("no match 4", "flow \t Ab {  ", null),
                makeTestData("no match 5", "flow \n Ab { }  ", null),
                makeTestData("no match 6", "flow Ab{}", null),
                makeTestData("simple 1", "flow Ab{(Bla);}", flow),
                makeTestData("simple 2", "flow \t Ab \n { \t(Bla); } \n ", flow),
                makeTestData("simple 3", "flow  \t  Ab /* bla */ \t {(Bla);} /* blu */ \n ", flow),
                makeTestData("simple 4", "flow   Ab // comment! \n { \t(Bla); } // comment, too!\n ", flow),
                makeTestData("complex", "flow Ab \t /* bla\n */ \t // com!\n \t { \t (Bla);\r\n/** blu */ }", flow)
        );
    }

    public ParseFlowTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseFlow<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Flows don't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
