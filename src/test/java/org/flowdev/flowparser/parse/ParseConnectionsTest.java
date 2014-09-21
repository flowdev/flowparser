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
import static org.flowdev.flowparser.util.PortUtil.newPort;
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
        Flow minFlow = addConnections(createFlow(new Operation().name("bla").type("Bla").ports(asList(new PortPair().isLast(true)))));
        Flow simpleFlow1 = addConnections(createFlow(
                        new Operation().name("doIt").type("DoIt").ports(asList(new PortPair().inPort(newPort("in")).isLast(true)))),
                new Connection().fromPort(newPort("in")).toPort(newPort("in")).toOp("doIt"));
        Flow simpleFlow2 = addConnections(createFlow(
                        new Operation().name("bla").type("Bla").ports(asList(new PortPair().inPort(newPort("in")).outPort(newPort("out")).isLast(true))),
                        new Operation().name("blue").type("Blue").ports(asList(new PortPair().inPort(newPort("in")).outPort(newPort("out")).isLast(true)))
                ), new Connection().fromPort(newPort("in")).toPort(newPort("in")).toOp("bla"),
                new Connection().fromOp("bla").fromPort(newPort("out")).toPort(newPort("in")).toOp("blue"),
                new Connection().fromOp("blue").fromPort(newPort("out")).toPort(newPort("out")),
                new Connection().fromPort(newPort("in2")).toPort(newPort("in")).toOp("blue"),
                new Connection().fromOp("blue").fromPort(newPort("out")).toPort(newPort("out2"))
        );
        Flow complexFlow = addConnections(createFlow(
                        new Operation().name("blaa").type("Bla").ports(asList(
                                new PortPair().inPort(newPort("i", 0)).outPort(newPort("o", 0)).isLast(true)
                        )),
                        new Operation().name("bluu").type("Blue").ports(asList(
                                new PortPair().inPort(newPort("i", 1)).outPort(newPort("o", 3)),
                                new PortPair().inPort(newPort("in")).outPort(newPort("o", 2)).isLast(true)
                        )),
                        new Operation().name("ab").type("Ab").ports(asList(new PortPair().outPort(newPort("out")).isLast(true)))
                ), new Connection().fromPort(newPort("i", 1)).toPort(newPort("i", 0)).toOp("blaa"),
                new Connection().fromOp("blaa").fromPort(newPort("o", 0)).toPort(newPort("i", 1)).toOp("bluu"),
                new Connection().fromOp("bluu").fromPort(newPort("o", 3)).toPort(newPort("o", 3)),
                new Connection().fromPort(newPort("in", 2)).toPort(newPort("in")).toOp("bluu"),
                new Connection().fromOp("bluu").fromPort(newPort("o", 2)).toPort(newPort("out2")),
                new Connection().fromOp("ab").fromPort(newPort("out")).toPort(newPort("out1"))
        );

        return asList(
                makeTestData("no match 1", "-> (Bla) -> ", null),
                makeTestData("min flow", "(Bla) \r\n\t ;", minFlow),
                makeTestData("(un)indexed port error", "-> (Blue) -> ;\nin2 -> in.2 (Blue) out.2 -> out2;", null),
                makeTestData("simple flow 1", "->doIt(DoIt);", simpleFlow1),
                makeTestData("simple flow 2", "-> (Bla) -> (Blue) -> ; in2 -> (Blue) ->out2;", simpleFlow2),
                makeTestData("complex flow", "i.1 -> i.0 blaa(Bla) o.0 -> i.1 bluu(Blue) o.3 -> ;\n" +
                        "  in.2 -> bluu() o.2 -> out2;\n" +
                        "  (Ab) -> out1;", complexFlow)
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
