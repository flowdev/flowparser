package org.flowdev.flowparser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowdev.base.Port;
import org.flowdev.base.data.PrettyPrinter;
import org.flowdev.flowparser.mustache.OutputFlowFileConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MainTest {

    private static final String JSON = "json";
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
	MainData data;
	MainConfig config;

	params.add(new String[] { MINI_FLOW });
	data = createData(MINI_FLOW);
	config = createConfig(JSON,
		Main.getOutputRoot(JSON, Arrays.asList(MINI_FLOW)));
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

    private static MainConfig createConfig(String... roots) {
	MainConfig config = new MainConfig();
	Map<String, String> rootMap = new HashMap<>();

	for (int i = 1; i < roots.length; i += 2) {
	    rootMap.put(roots[i - 1], roots[i]);
	}

	config.outputFlowFile = new OutputFlowFileConfig();
	config.outputFlowFile.roots = rootMap;
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
	public Port<MainData> getIn() {
	    return new Port<MainData>() {
		@Override
		public void send(MainData data) {
		    myData = data;
		}
	    };
	}

	@Override
	public Port<MainConfig> getConfig() {
	    return new Port<MainConfig>() {
		@Override
		public void send(MainConfig data) {
		    myConfig = data;
		}
	    };
	}

    }
}
