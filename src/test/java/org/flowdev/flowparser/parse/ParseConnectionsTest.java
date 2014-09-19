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
        Flow minFlow = addConnections(createFlow(new Operation().name("bla").type("Bla").ports(asList(new PortPair()))));
        Flow simpleFlow1 = addConnections(createFlow(
                        new Operation().name("doIt").type("DoIt").ports(asList(new PortPair().inPort("in")))),
                new Connection().fromPort("in").toPort("in").toOp("doIt"));
        Flow simpleFlow2 = addConnections(createFlow(
                        new Operation().name("bla").type("Bla").ports(asList(new PortPair().inPort("in").outPort("out"))),
                        new Operation().name("blue").type("Blue").ports(asList(new PortPair().inPort("in").outPort("out")))
                ), new Connection().fromPort("in").toPort("in").toOp("bla"),
                new Connection().fromOp("bla").fromPort("out").toPort("in").toOp("blue"),
                new Connection().fromOp("blue").fromPort("out").toPort("out"),
                new Connection().fromPort("in2").toPort("in").toOp("blue"),
                new Connection().fromOp("blue").fromPort("out").toPort("out2")
        );
        Flow complexFlow = addConnections(createFlow(
                        new Operation().name("blaa").type("Bla").ports(asList(
                                new PortPair().inPort("i").hasInPortIndex(true).inPortIndex(0)
                                        .outPort("o").hasOutPortIndex(true).outPortIndex(0)
                        )),
                        new Operation().name("bluu").type("Blue").ports(asList(
                                new PortPair().inPort("i").hasInPortIndex(true).inPortIndex(1)
                                        .outPort("o").hasOutPortIndex(true).outPortIndex(3),
                                new PortPair().inPort("in")
                                        .outPort("o").hasOutPortIndex(true).outPortIndex(2)
                        )),
                        new Operation().name("ab").type("Ab").ports(asList(new PortPair().outPort("out")))
                ), new Connection().fromPort("i").hasFromPortIndex(true).fromPortIndex(1)
                        .toPort("i").hasToPortIndex(true).toPortIndex(0).toOp("blaa"),
                new Connection().fromOp("blaa").fromPort("o").hasFromPortIndex(true).fromPortIndex(0)
                        .toPort("i").hasToPortIndex(true).toPortIndex(1).toOp("bluu"),
                new Connection().fromOp("bluu").fromPort("o").hasFromPortIndex(true).fromPortIndex(3)
                        .toPort("o").hasToPortIndex(true).toPortIndex(3),
                new Connection().fromPort("in").hasFromPortIndex(true).fromPortIndex(2).toPort("in").toOp("bluu"),
                new Connection().fromOp("bluu").fromPort("o").hasFromPortIndex(true).fromPortIndex(2).toPort("out2"),
                new Connection().fromOp("ab").fromPort("out").toPort("out1")
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
