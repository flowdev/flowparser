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
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseChainBeginTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Operation maxOpBlaNoPorts = new Operation().name("bla").type("Bla").portPairs(singletonList(
                new PortPair().inPort(newPort("in").srcPos(2)).outPort(newPort("out").srcPos(7))));
        Operation maxOpBlaNoPorts2 = new Operation().name("bla").type("Bla").portPairs(singletonList(
                new PortPair().inPort(newPort("in").srcPos(15)).outPort(newPort("out").srcPos(20))));
        Connection maxConnNoTypeNoPorts = new Connection().fromPort(newPort("in")).toPort(maxOpBlaNoPorts.portPairs().get(0).inPort());
        Connection maxConnTypeNoPorts = new Connection().fromPort(newPort("in")).toPort(maxOpBlaNoPorts2.portPairs().get(0).inPort())
                .dataType("BlaFlowData").showDataType(true);

        Operation maxOpBluPorts = new Operation().name("bla").type("Blu").portPairs(singletonList(
                new PortPair().inPort(newPort("xIn", 1).srcPos(22)).outPort(newPort("outY", 123).srcPos(38))));
        Connection maxConnTypePorts = new Connection().fromPort(newPort("ourIn")).toPort(maxOpBluPorts.portPairs().get(0).inPort())
                .dataType("BlaFlowData").showDataType(true);

        Operation minOpBlaNoPorts = new Operation().name("bla").type("Bla").portPairs(singletonList(
                new PortPair().outPort(newPort("out").srcPos(5))));
        Operation minOpBlaPorts = new Operation().name("bla").portPairs(singletonList(
                new PortPair().inPort(null).outPort(newPort("error", 3).srcPos(6))));
        Operation minOpBluePorts = new Operation().name("bla").type("Blue").portPairs(singletonList(
                new PortPair().inPort(null).outPort(newPort("error", 3).srcPos(10))));

        return asList( //
                makeTestData("no match 1", "->(B)", null),
                makeTestData("no match 2", "(B)", null),
                makeTestData("simple max 1", "->(Bla)", createConnectionOperation(maxConnNoTypeNoPorts, maxOpBlaNoPorts, 2)),
                makeTestData("simple max 2", "[BlaFlowData]->(Bla)", createConnectionOperation(maxConnTypeNoPorts, maxOpBlaNoPorts2, 15)),
                makeTestData("full max", "ourIn [BlaFlowData]-> xIn.1   bla(Blu)outY.123",
                        createConnectionOperation(maxConnTypePorts, maxOpBluPorts, 30)),
                makeTestData("simple min 1", "(Bla)", createConnectionOperation(null, minOpBlaNoPorts, 0)),
                makeTestData("simple min 2", "bla() error.3", createConnectionOperation(null, minOpBlaPorts, 0)),
                makeTestData("full min", "bla(Blue) error.3", createConnectionOperation(null, minOpBluePorts, 0))
        );
    }

    private static List<Object> createConnectionOperation(Connection conn, Operation op, int srcPos) {
        ArrayList<Object> list = new ArrayList<>(2);
        Operation newOp = new Operation().name(op.name()).type(op.type()).srcPos(srcPos).portPairs(op.portPairs());
        list.add(conn == null ? null : conn.toOp(newOp));
        list.add(newOp);
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
