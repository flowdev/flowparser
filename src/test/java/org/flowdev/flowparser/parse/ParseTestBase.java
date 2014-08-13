package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.data.SourceData;
import org.flowdev.parser.op.ParserParams;
import org.junit.Test;

import static org.flowdev.base.data.PrettyPrinter.prettyPrint;
import static org.junit.Assert.*;


public abstract class ParseTestBase {
    final ParserData parserData;
    private final Object expectedValue;
    private final Filter<ParserData, NoConfig> parser;
    private ParserData actualResultParserData;

    public ParseTestBase(ParserData parserData, Object expectedValue) {
        this.parserData = parserData;
        this.expectedValue = expectedValue;

        ParserParams<ParserData> params = new ParserParams<>();
        params.getParserData = data -> data;
        params.setParserData = (data, subdata) -> {
            data = subdata;
            return subdata;
        };
        this.parser = makeParser(params);
        this.parser.setErrorPort(e -> {
            e.printStackTrace();
            throw new RuntimeException(e);
//            fail(e.getMessage());
        });
        this.parser.setOutPort(data -> this.actualResultParserData = data);
    }

    protected abstract Filter<ParserData, NoConfig> makeParser(ParserParams<ParserData> params);

    protected abstract void checkResultValue(Object expectedValue, Object actualValue);

    @Test
    public void testParser() {
        parser.getInPort().send(parserData);
        Object actualValue = actualResultParserData.result().value();
        if (expectedValue == null || expectedValue == Void.TYPE) {
            if (actualValue != null) {
                System.err.println("Unexpected actual value: " + prettyPrint(actualValue));
            }
            assertNull("Actual value should be null.", actualValue);
        } else {
            assertEquals("Expected and actual value don't have the same class.", expectedValue.getClass(), actualValue.getClass());

            checkResultValue(expectedValue, actualValue);
        }
        if (expectedValue == null) {
            assertNotNull(actualResultParserData.result().feedback());
            assertNotNull(actualResultParserData.result().feedback().errors());
            assertNotSame(0, actualResultParserData.result().feedback().errors().size());
            System.out.println("Expected errors: " + actualResultParserData.result().feedback().errors());
        } else {
            if (actualResultParserData.result().feedback() != null) {
                if (!actualResultParserData.result().feedback().errors().isEmpty()) {
                    System.err.println("Unexpected errors: " + actualResultParserData.result().feedback().errors());
                }
                assertTrue("No errors expected.", actualResultParserData.result().feedback().errors().isEmpty());
            }
        }
    }

    public static Object[] makeTestData(String srcName, String srcContent, Object expectedValue) {
        ParserData parserData = new ParserData();
        parserData.source(new SourceData().name(srcName).pos(0).content(srcContent));

        return new Object[]{parserData, expectedValue};
    }
}
