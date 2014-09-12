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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.junit.Assert.assertEquals;


/**
 * Test ParseConnections with this possible text input:
 * <p>
 * ( optInPort  [OptDataType]-> optInPort )? opName(OpType) optOutPort
 * ( [OptDataType]-> optInPort opName(OpType) optOutPort )*
 * ( [OptDataType]-> optOutPort )?
 */
@RunWith(Parameterized.class)
public class ParseConnectionsTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        Flow minFlow = addConnections(createFlow(new Operation().name("bla").type("Bla").ports(asList(new PortPair().outPort("out")))));
        Flow simpleFlow = addConnections(createFlow(
                        new Operation().name("bla").type("Bla").ports(asList(new PortPair().inPort("in").outPort("out"))),
                        new Operation().name("blue").type("Blue").ports(asList(new PortPair().inPort("in").outPort("out")))
                ), new Connection().fromPort("in").toPort("in").toOp("bla"),
                new Connection().fromOp("bla").fromPort("out").toPort("in").toOp("blue"),
                new Connection().fromOp("blue").fromPort("out").toPort("out"),
                new Connection().fromPort("in2").toPort("in").toOp("blue"),
                new Connection().fromOp("blue").fromPort("out").toPort("out2")
        );

        return asList(
                makeTestData("no match 1", "-> (Bla) -> ", null),
                makeTestData("min flow", "(Bla) \r\n\t ;", minFlow),
                makeTestData("simple flow", "-> (Bla) -> (Blue) -> ; in2 -> (Blue) ->out2;", simpleFlow),
                makeTestData("(un)indexed port error", "-> (Blue) -> ;\nin2 -> in.2 (Blue) out.2 -> out2;", null)
        );
    }

    private static Flow createFlow(Operation... ops) {
        return new Flow().operations(asList(ops));
    }

    private static Flow addConnections(Flow flow, Connection... conns) {
        return flow.connections(conns == null ? emptyList() : asList(conns));
    }

    public ParseConnectionsTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseConnections<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        assertEquals("Connections don't match.", prettyPrint(expectedValue), prettyPrint(actualValue));
    }
}
