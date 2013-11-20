package org.flowdev.flowparser;

import org.flowdev.base.Port;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.output.OutputAllFormatsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainTest {

    private static final String FORMAT = "graphviz";
    private static final String MINI_FLOW = "./src/test/flow/flowparser/mini.flow";
    private TestMainFlow flow;
    private String[] args;
    private MainData expectedData;
    private MainConfig expectedConfig;

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
        MainConfig  config = createConfig(FORMAT);
        params.add(data);
        params.add(config);
        paramsList.add(params.toArray());

        return paramsList;
    }

    private static MainData createData(String fileName) {
        MainData data = new MainData();
        data.fileName = fileName;
        return data;
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
            return new Port<MainData>() {
                @Override
                public void send(MainData data) {
                    myData = data;
                }
            };
        }

        @Override
        public Port<MainConfig> getConfigPort() {
            return new Port<MainConfig>() {
                @Override
                public void send(MainConfig data) {
                    myConfig = data;
                }
            };
        }

    }
}
