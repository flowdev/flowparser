package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Operation;
import org.flowdev.flowparser.data.PortPair;
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
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseChainBeginTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Operation opBlaNoPorts = new Operation().name("bla").type("Bla").ports(singletonList(new PortPair().inPort("in").outPort("out")));
        Operation opBlaPorts = new Operation().name("bla").ports(singletonList(
                new PortPair().inPort("in").hasInPortIndex(true).inPortIndex(2).outPort("error")));
        Operation opBluPorts = new Operation().name("bla").type("Blu").ports(singletonList(
                new PortPair().inPort("xIn").hasInPortIndex(true).inPortIndex(1).outPort("outY").hasOutPortIndex(true).outPortIndex(123)));

        return asList( //
                makeTestData("no match 1", "->(B)", null), //
                makeTestData("no match 2", "(B)", null) //
//                makeTestData("simple 1", "->(Bla)", createDataTypeOperation(null, opBlaNoPorts)), //
//                makeTestData("simple 2", "-> in.2 \t bla() \t error ", createDataTypeOperation(null, opBlaPorts)), //
//                makeTestData("simple 3", "[DataType]-> /* comm */\n \t xIn.1   bla(Blu)outY.123",
//                        createDataTypeOperation("DataType", opBluPorts))  //
        );
    }

    private static List<Object> createDataTypeOperation(String dataType, Operation op) {
        ArrayList<Object> list = new ArrayList<>(2);
        list.add(dataType);
        list.add(op);
        return list;
    }

    public ParseChainBeginTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseChainBegin<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Operation doesn't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
