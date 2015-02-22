package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.CoreFlow.CoreFlowConfig;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.data.SourceData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.flowdev.flowparser.output.OutputAllFormats.OutputAllFormatsConfig;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainTest {

    private static final String FORMAT = "adoc";
    private static final String MINI_FLOW = "./src/test/flow/flowparser/mini.flow";
    private TestMainFlow flow;
    private final String[] args;
    private final MainData expectedData;
    private final CoreFlowConfig expectedConfig;

    public MainTest(String[] args, MainData expectedData,
                    CoreFlowConfig expectedConfig) {
        this.args = args;
        this.expectedData = expectedData;
        this.expectedConfig = expectedConfig;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> checkPorts() {
        List<Object[]> paramsList = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        params.add(new String[]{MINI_FLOW});

        MainData data = createData(MINI_FLOW);
        CoreFlowConfig config = createConfig(FORMAT);
        params.add(data);
        params.add(config);
        paramsList.add(params.toArray());

        return paramsList;
    }

    private static MainData createData(String fileName) {
        return new MainData().parserData(new ParserData().source(new SourceData().name(fileName)));
    }

    private static CoreFlowConfig createConfig(String... formats) {
        return new CoreFlowConfig().outputAllFormats(new OutputAllFormatsConfig().formats(asList(formats)));
    }

    @Before
    public void setUp() throws Exception {
        flow = new TestMainFlow();
        Main.setMainFlow(flow);
    }

    @Test
    public void testMain() throws IOException {
        Main.main(args);
        String exp = PrettyPrinter.prettyPrint(expectedData);
        String act = PrettyPrinter.prettyPrint(flow.myData);
        assertEquals("Asserting data: ", exp, act);
        exp = PrettyPrinter.prettyPrint(expectedConfig);
        act = PrettyPrinter.prettyPrint(flow.myConfig);
        assertEquals("Asserting config: ", exp, act);
    }

    private static class TestMainFlow implements IMainFlow {
        MainData myData;
        CoreFlowConfig myConfig;

        @Override
        public Port<MainData> getInPort() {
            return data -> myData = data;
        }

        @Override
        public Port<CoreFlowConfig> getConfigPort() {
            return data -> myConfig = data;
        }

        @Override
        public void setErrorPort(Port<Throwable> port) {
        }

    }
}
