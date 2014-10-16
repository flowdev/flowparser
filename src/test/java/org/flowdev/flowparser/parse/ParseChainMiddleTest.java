package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseChainMiddleTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Operation opBlaNoPorts = new Operation().name("bla").type("Bla").srcPos(2)
                .inPorts(singletonList(newPort(2, "in"))).outPorts(singletonList(newPort(7, "out")));
        Operation opBlaPorts = new Operation().name("bla").srcPos(10)
                .inPorts(singletonList(newPort(3, "in", 2))).outPorts(singletonList(newPort(18, "error")));
        Operation opBluPorts = new Operation().name("bla").type("Blu").srcPos(35)
                .inPorts(singletonList(newPort(27, "xIn", 1))).outPorts(singletonList(newPort(43, "outY", 123)));

        return asList( //
                makeTestData("no match 1", "->(B)", null), //
                makeTestData("no match 2", "(Bla)", null), //
                makeTestData("simple 1", "->(Bla)", createDataTypeOperation(null, opBlaNoPorts)), //
                makeTestData("simple 2", "-> in.2 \t bla() \t error ", createDataTypeOperation(null, opBlaPorts)), //
                makeTestData("simple 3", "[DataType]-> /* comm */\n \t xIn.1   bla(Blu)outY.123",
                        createDataTypeOperation("DataType", opBluPorts))  //
        );
    }

    private static List<Object> createDataTypeOperation(String dataType, Operation op) {
        ArrayList<Object> list = new ArrayList<>(2);
        list.add(dataType);
        list.add(op);
        return list;
    }

    public ParseChainMiddleTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseChainMiddle<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Datatype or Operation doesn't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
