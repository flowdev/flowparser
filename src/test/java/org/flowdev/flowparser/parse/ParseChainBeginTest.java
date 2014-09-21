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
import static org.flowdev.flowparser.util.PortUtil.emptyPort;
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseChainBeginTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Operation maxOpBlaNoPorts = new Operation().name("bla").type("Bla").ports(singletonList(
                new PortPair().inPort(newPort("in")).outPort(newPort("out"))));
        Connection maxConnNoTypeNoPorts = new Connection().fromPort(newPort("in")).toOp("bla").toPort(newPort("in"));
        Connection maxConnTypeNoPorts = new Connection().fromPort(newPort("in")).toOp("bla").toPort(newPort("in"))
                .dataType("BlaFlowData").showDataType(true);

        Operation maxOpBluPorts = new Operation().name("bla").type("Blu").ports(singletonList(
                new PortPair().inPort(newPort("xIn", 1)).outPort(newPort("outY", 123))));
        Connection maxConnTypePorts = new Connection().fromPort(newPort("ourIn")).toOp("bla").toPort(newPort("xIn", 1))
                .dataType("BlaFlowData").showDataType(true);

        Operation minOpBlaNoPorts = new Operation().name("bla").type("Bla").ports(singletonList(
                new PortPair().outPort(newPort("out"))));
        Operation minOpBlaPorts = new Operation().name("bla").ports(singletonList(
                new PortPair().inPort(emptyPort()).outPort(newPort("error", 3))));
        Operation minOpBluePorts = new Operation().name("bla").type("Blue").ports(singletonList(
                new PortPair().inPort(emptyPort()).outPort(newPort("error", 3))));

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
