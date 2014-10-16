package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.Connection;
import org.flowdev.flowparser.data.Flow;
import org.flowdev.flowparser.data.Operation;
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
        Operation doIt = new Operation().name("doIt").type("DoIt").srcPos(2).inPorts(asList(newPort(2, "in")));
        Flow minFlow = addConnections(createFlow(new Operation().name("bla").type("Bla")));
        Flow simpleFlow1 = addConnections(createFlow(doIt),
                new Connection().fromPort(newPort(0, "in")).toPort(doIt.inPorts().get(0)).toOp(doIt));

        Operation blue = new Operation().name("blue").type("Blue").srcPos(3)
                .inPorts(asList(newPort(3, "in"))).outPorts(asList(newPort(10, "out")));
        Flow simpleFlow2 = addConnections(createFlow(blue),
                new Connection().fromPort(newPort(0, "in")).toPort(blue.inPorts().get(0)).toOp(blue),
                new Connection().fromOp(blue).fromPort(blue.outPorts().get(0)).toPort(newPort(13, "out")),
                new Connection().fromPort(newPort(15, "in2")).toPort(blue.inPorts().get(0)).toOp(blue),
                new Connection().fromPort(newPort(30, "in3")).toPort(blue.inPorts().get(0)).toOp(blue)
        );

        Operation bla = new Operation().name("bla").type("Bla").srcPos(3).inPorts(asList(newPort(3, "in")))
                .outPorts(asList(newPort(9, "out")));
        Operation blue2 = new Operation().name("blue").type("Blue").srcPos(12).inPorts(asList(newPort(12, "in")))
                .outPorts(asList(newPort(19, "out"), newPort(38, "out2")));
        Flow simpleFlow3 = addConnections(createFlow(bla, blue2),
                new Connection().fromPort(newPort(0, "in")).toPort(bla.inPorts().get(0)).toOp(bla),
                new Connection().fromOp(bla).fromPort(bla.outPorts().get(0)).toPort(blue2.inPorts().get(0)).toOp(blue2),
                new Connection().fromOp(blue2).fromPort(blue2.outPorts().get(0)).toPort(newPort(22, "out")),
                new Connection().fromPort(newPort(24, "in2")).toPort(blue2.inPorts().get(0)).toOp(blue2),
                new Connection().fromOp(blue2).fromPort(blue2.outPorts().get(1)).toPort(newPort(45, "out2"))
        );

        Operation blaa = new Operation().name("blaa").type("Bla").srcPos(11).inPorts(asList(newPort(7, "i", 0)))
                .outPorts(asList(newPort(21, "o", 0)));
        Operation bluu = new Operation().name("bluu").type("Blue").srcPos(32)
                .inPorts(asList(newPort(28, "i", 1), newPort(62, "in")))
                .outPorts(asList(newPort(43, "o", 3), newPort(69, "o", 2)));
        Operation ab = new Operation().name("ab").type("Ab").srcPos(84).outPorts(asList(newPort(89, "out")));
        Flow complexFlow = addConnections(createFlow(blaa, bluu, ab),
                new Connection().fromPort(newPort(0, "i", 1)).toPort(blaa.inPorts().get(0)).toOp(blaa),
                new Connection().fromOp(blaa).fromPort(blaa.outPorts().get(0)).toPort(bluu.inPorts().get(0)).toOp(bluu),
                new Connection().fromOp(bluu).fromPort(bluu.outPorts().get(0)).toPort(newPort(50, "o", 3)),
                new Connection().fromPort(newPort(54, "in", 2)).toPort(bluu.inPorts().get(1)).toOp(bluu),
                new Connection().fromOp(bluu).fromPort(bluu.outPorts().get(1)).toPort(newPort(76, "out2")),
                new Connection().fromOp(ab).fromPort(ab.outPorts().get(0)).toPort(newPort(92, "out1"))
        );

        return asList(
                makeTestData("no match 1", "-> (Bla) -> ", null),
                makeTestData("min flow", "(Bla) \r\n\t ;", minFlow),
                makeTestData("(un)indexed port error", "-> (Blue) -> ;\nin2 -> in.2 (Blue) out.2 -> out2;", null),
                makeTestData("simple flow 1", "->doIt(DoIt);", simpleFlow1),
                makeTestData("multiple input ports flow", "-> (Blue) -> ; in2 -> (Blue); in3 -> blue();", simpleFlow2),
                makeTestData("split out port error", "-> (Blue) -> ; in2 -> (Blue) ->out2;", null),
                makeTestData("simple flow 3", "-> (Bla) -> (Blue) -> ; in2 -> (Blue) out2 ->out2;", simpleFlow3),
                makeTestData("complex flow", "i.1 -> i.0 blaa(Bla) o.0 -> i.1 bluu(Blue) o.3 -> ;\n"                  +
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
