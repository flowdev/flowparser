package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.*;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.op.ParserParams;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.flowdev.flowparser.util.PortUtil.newPort;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParseFlowFileTest extends ParseTestBase {
    @Parameterized.Parameters
    public static Collection<?> generateTestDatas() {
        return asList( //
                makeTestData("empty", "", null), //
                makeTestData("no match 1", "version 2.0  flow Acdc", null), //
                makeTestData("no match 3", " /* bla */version \t 1.234 \n flow Acdc {} _t", null), //
                makeTestData("simple 1", "version 1.234 flow Acdc{(Bla);}", createFlowFile("simple 1")), //
                makeTestData("simple 2", "\t version \t 1.234\n flow \t Acdc { (Bla);} \n ", createFlowFile("simple 2")), //
                makeTestData("simple 3", " /* bla */version \t 1.234 \n flow  Acdc { \t (Bla);\n \t }", createFlowFile("simple 3")), //
                makeTestData("simple 4", " // comment! \n version \t 1.234 flow Acdc {(Bla);} \r\n \t ", createFlowFile("simple 4")), //
                makeTestData("complex", " /* bla\n */ \t // com!\n \t version \t 1.234 \r\n/** blu */ flow \t Acdc /* blo \n */ { \t(Bla); // com2!\r\n } ",
                        createFlowFile("complex"))
        );
    }

    private static FlowFile createFlowFile(String name) {
        Flow flow = new Flow().name("Acdc").operations(asList(
                new Operation().name("bla").type("Bla").outPorts(asList(newPort(0, "out")))
        )).connections(Collections.<Connection>emptyList());

        return new FlowFile().fileName(name).version(new Version().political(1).major(234))
                .flows(asList(flow));
    }

    public ParseFlowFileTest(ParserData parserData, Object expectedValue) {
        super(parserData, expectedValue);
    }

    @Override
    protected Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params) {
        return new ParseFlowFile<>(params);
    }

    @Override
    protected void checkResultValue(Object expectedValue, Object actualValue) {
        FlowFile expected = (FlowFile) expectedValue;
        FlowFile actual = (FlowFile) actualValue;
        assertEquals("File name doesn't match.", expected.fileName(), actual.fileName());
        assertEquals("Political version doesn't match.", expected.version().political(), actual.version().political());
        assertEquals("Major version doesn't match.", expected.version().major(), actual.version().major());
    }
}
