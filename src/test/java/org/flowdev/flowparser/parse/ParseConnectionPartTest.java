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
import static java.util.Collections.singletonList;
import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseConnectionPartTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Operation opBlaNoPorts = new Operation().name("bla").type("Bla").srcPos(0).inPorts(singletonList(newPort(0, "in")))
                .outPorts(singletonList(newPort(5, "out")));
        Operation opBlaPorts = new Operation().name("bla").srcPos(7).inPorts(singletonList(newPort(0, "in", 2)))
                .outPorts(singletonList(newPort(15, "error")));
        Operation opBluPorts = new Operation().name("bla").type("Blu").srcPos(8).inPorts(singletonList(newPort(0, "xIn", 1)))
                .outPorts(singletonList(newPort(16, "outY", 123)));

        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "()", null), //
                makeTestData("no match 2", "bla", null), //
                makeTestData("no match 3", "Bla", null), //
                makeTestData("simple 1", "(Bla)", opBlaNoPorts), //
                makeTestData("simple 2", "in.2 \t bla() \t error ", opBlaPorts), //
                makeTestData("simple 3", "xIn.1   bla(Blu)outY.123", opBluPorts)  //
        );
    }

    public ParseConnectionPartTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params) {
        return new ParseConnectionPart<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Operation doesn't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
