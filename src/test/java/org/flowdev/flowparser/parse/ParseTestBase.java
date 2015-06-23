package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.flowparser.data.MainData;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.data.SourceData;
import org.flowdev.parser.op.ParserParams;
import org.junit.Test;

import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.junit.Assert.*;


public abstract class ParseTestBase {
    final ParserData parserData;
    private final Object expectedValue;
    private final Filter<MainData, NoConfig> parser;
    private ParserData actualResultParserData;

    public ParseTestBase(ParserData parserData, Object expectedValue) {
        this.parserData = parserData;
        this.expectedValue = expectedValue;

        ParserParams<MainData> params = new ParserParams<>();
        params.getParserData = dat -> dat.parserData();
        params.setParserData = (dat, subdata) -> {
            dat.parserData(subdata);
            return dat;
        };
        this.parser = makeParser(params);
        this.parser.setErrorPort(e -> {
            e.printStackTrace();
            throw new RuntimeException(e);
//            fail(e.getMessage());
        });
        this.parser.setOutPort(dat -> this.actualResultParserData = dat.parserData());
    }

    protected abstract Filter<MainData, NoConfig> makeParser(ParserParams<MainData> params);

    protected abstract void checkResultValue(Object expectedValue, Object actualValue);

    @Test
    public void testParser() {
        parser.getInPort().send(new MainData().parserData(parserData));
        Object actualValue = actualResultParserData.result().value();

        if (expectedValue == null || expectedValue == Void.TYPE) {
            if (actualValue != null) {
                System.err.println("Unexpected actual value: " + prettyPrint(actualValue));
            }
            assertNull("Actual value should be null.", actualValue);

            if (expectedValue == null) {
                assertNotNull(actualResultParserData.result().feedback());
                assertNotNull(actualResultParserData.result().feedback().getErrors());
                assertNotSame(0, actualResultParserData.result().feedback().getErrors().size());
                System.out.println("Expected getErrors: " + actualResultParserData.result().feedback().getErrors());
            }
        } else {
//            assertEquals("Expected and actual value don't have the same class.", expectedValue.getClass(), actualValue.getClass());
            if (actualResultParserData.result().feedback() != null) {
                if (!actualResultParserData.result().feedback().getErrors().isEmpty()) {
                    System.err.println("Unexpected getErrors: " + actualResultParserData.result().feedback().getErrors());
                }
                assertTrue("No getErrors expected.", actualResultParserData.result().feedback().getErrors().isEmpty());
            }

            checkResultValue(expectedValue, actualValue);
        }
    }

    public static Object[] makeTestData(String srcName, String srcContent, Object expectedValue) {
        ParserData parserData = new ParserData().source(new SourceData().name(srcName).pos(0).content(srcContent));

        return new Object[]{parserData, expectedValue};
    }
}
