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
        Operation doIt = new Operation().name("doIt").type("DoIt").srcPos(2)
                .ports(asList(new PortPair().inPort(newPort("in").srcPos(2)).isLast(true)));
        Flow minFlow = addConnections(createFlow(new Operation().name("bla").type("Bla").ports(asList(new PortPair().isLast(true)))));
        Flow simpleFlow1 = addConnections(createFlow(doIt),
                new Connection().fromPort(newPort("in")).toPort(doIt.ports().get(0).inPort()).toOp(doIt));

        Operation blue = new Operation().name("blue").type("Blue").srcPos(3)
                .ports(asList(new PortPair().inPort(newPort("in").srcPos(3)).outPort(newPort("out").srcPos(10)).isLast(true)));
        Flow simpleFlow2 = addConnections(createFlow(blue),
                new Connection().fromPort(newPort("in")).toPort(blue.ports().get(0).inPort()).toOp(blue),
                new Connection().fromOp(blue).fromPort(blue.ports().get(0).outPort()).toPort(newPort("out").srcPos(13)),
                new Connection().fromPort(newPort("in2").srcPos(15)).toPort(blue.ports().get(0).inPort()).toOp(blue),
                new Connection().fromPort(newPort("in3").srcPos(30)).toPort(blue.ports().get(0).inPort()).toOp(blue)
        );

        Operation bla = new Operation().name("bla").type("Bla").srcPos(3)
                .ports(asList(new PortPair().inPort(newPort("in").srcPos(3)).outPort(newPort("out").srcPos(9)).isLast(true)));
        Operation blue2 = new Operation().name("blue").type("Blue").srcPos(12).ports(asList(
                new PortPair().inPort(newPort("in").srcPos(12)).outPort(newPort("out").srcPos(19)),
                new PortPair().outPort(newPort("out2").srcPos(38)).isLast(true)));
        Flow simpleFlow3 = addConnections(createFlow(bla, blue2),
                new Connection().fromPort(newPort("in")).toPort(bla.ports().get(0).inPort()).toOp(bla),
                new Connection().fromOp(bla).fromPort(bla.ports().get(0).outPort()).toPort(blue2.ports().get(0).inPort()).toOp(blue2),
                new Connection().fromOp(blue2).fromPort(blue2.ports().get(0).outPort()).toPort(newPort("out").srcPos(22)),
                new Connection().fromPort(newPort("in2").srcPos(24)).toPort(blue2.ports().get(0).inPort()).toOp(blue2),
                new Connection().fromOp(blue2).fromPort(blue2.ports().get(1).outPort()).toPort(newPort("out2").srcPos(45))
        );

        Operation blaa = new Operation().name("blaa").type("Bla").srcPos(11).ports(asList(
                new PortPair().inPort(newPort("i", 0).srcPos(7)).outPort(newPort("o", 0).srcPos(21)).isLast(true)
        ));
        Operation bluu = new Operation().name("bluu").type("Blue").srcPos(32).ports(asList(
                new PortPair().inPort(newPort("i", 1).srcPos(28)).outPort(newPort("o", 3).srcPos(43)),
                new PortPair().inPort(newPort("in").srcPos(62)).outPort(newPort("o", 2).srcPos(69)).isLast(true)
        ));
        Operation ab = new Operation().name("ab").type("Ab").srcPos(84)
                .ports(asList(new PortPair().outPort(newPort("out").srcPos(89)).isLast(true)));
        Flow complexFlow = addConnections(createFlow(blaa, bluu, ab),
                new Connection().fromPort(newPort("i", 1)).toPort(blaa.ports().get(0).inPort()).toOp(blaa),
                new Connection().fromOp(blaa).fromPort(blaa.ports().get(0).outPort()).toPort(bluu.ports().get(0).inPort()).toOp(bluu),
                new Connection().fromOp(bluu).fromPort(bluu.ports().get(0).outPort()).toPort(newPort("o", 3).srcPos(50)),
                new Connection().fromPort(newPort("in", 2).srcPos(54)).toPort(bluu.ports().get(1).inPort()).toOp(bluu),
                new Connection().fromOp(bluu).fromPort(bluu.ports().get(1).outPort()).toPort(newPort("out2").srcPos(76)),
                new Connection().fromOp(ab).fromPort(ab.ports().get(0).outPort()).toPort(newPort("out1").srcPos(92))
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
