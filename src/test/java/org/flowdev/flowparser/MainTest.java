package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.output.OutputAllFormatsConfig;
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
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainTest {

    private static final String FORMAT = "wiki";
    private static final String MINI_FLOW = "./src/test/flow/flowparser/mini.flow";
    private TestMainFlow flow;
    private final String[] args;
    private final MainData expectedData;
    private final MainConfig expectedConfig;

    public MainTest(String[] args, MainData expectedData,
                    MainConfig expectedConfig) {
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
        MainConfig config = createConfig(FORMAT);
        params.add(data);
        params.add(config);
        paramsList.add(params.toArray());

        return paramsList;
    }

    private static MainData createData(String fileName) {
        return new MainData().parserData(new ParserData().source(new SourceData().name(fileName)));
    }

    private static MainConfig createConfig(String... formats) {
        MainConfig config = new MainConfig();
        config.outputAllFormats = new OutputAllFormatsConfig();
        config.outputAllFormats.formats = asList(formats);
        return config;
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
        MainConfig myConfig;

        @Override
        public Port<MainData> getInPort() {
            return data -> myData = data;
        }

        @Override
        public Port<MainConfig> getConfigPort() {
            return data -> myConfig = data;
        }

        @Override
        public void setErrorPort(Port<Throwable> port) {
        }

    }
}
