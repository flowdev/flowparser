package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Connection;
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
        Operation maxOpBlaNoPorts = new Operation().name("bla").type("Bla").ports(singletonList(new PortPair().inPort("in").outPort("out")));
        Connection maxConnNoTypeNoPorts = new Connection().fromPort("in").hasFromPortIndex(false).toOp("bla").toPort("in").hasToPortIndex(false);
        Connection maxConnTypeNoPorts = new Connection().fromPort("in").hasFromPortIndex(false)
                .toOp("bla").toPort("in").hasToPortIndex(false).dataType("BlaFlowData").showDataType(true);

        Operation maxOpBluPorts = new Operation().name("bla").type("Blu").ports(singletonList(
                new PortPair().inPort("xIn").hasInPortIndex(true).inPortIndex(1).outPort("outY").hasOutPortIndex(true).outPortIndex(123)));
        Connection maxConnTypePorts = new Connection().fromPort("ourIn").hasFromPortIndex(false)
                .toOp("bla").toPort("xIn").hasToPortIndex(true).toPortIndex(1).dataType("BlaFlowData").showDataType(true);

        Operation minOpBlaNoPorts = new Operation().name("bla").type("Bla").ports(singletonList(new PortPair().outPort("out")));
        Operation minOpBlaPorts = new Operation().name("bla").ports(singletonList(
                new PortPair().outPort("error").hasOutPortIndex(true).outPortIndex(3)));
        Operation minOpBluePorts = new Operation().name("bla").type("Blue").ports(singletonList(
                new PortPair().outPort("error").hasOutPortIndex(true).outPortIndex(3)));

        return asList( //
                makeTestData("no match 1", "->(B)", null),
                makeTestData("no match 2", "(B)", null),
                makeTestData("simple max 1", "->(Bla)", createConnectionOperation(maxConnNoTypeNoPorts, maxOpBlaNoPorts)),
                makeTestData("simple max 2", "[BlaFlowData]->(Bla)", createConnectionOperation(maxConnTypeNoPorts, maxOpBlaNoPorts)),
                makeTestData("full max", "ourIn [BlaFlowData]-> xIn.1   bla(Blu)outY.123", createConnectionOperation(maxConnTypePorts, maxOpBluPorts)),
                makeTestData("simple min 1", "(Bla)", createConnectionOperation(null, minOpBlaNoPorts)),
                makeTestData("simple min 2", "bla() error.3", createConnectionOperation(null, minOpBlaPorts)),
                makeTestData("full min", "bla(Blue) error.3", createConnectionOperation(null, minOpBluePorts))
        );
    }

    private static List<Object> createConnectionOperation(Connection conn, Operation op) {
        ArrayList<Object> list = new ArrayList<>(2);
        list.add(conn);
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
        assertEquals("Connection or Operation doesn't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
