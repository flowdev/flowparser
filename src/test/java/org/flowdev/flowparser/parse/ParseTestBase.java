package org.flowdev.flowparser.parse;

import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.Filter;
import org.flowdev.parser.data.ParserData;
import org.flowdev.parser.data.SourceData;
import org.flowdev.parser.op.ParserParams;
import org.junit.Test;

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
        checkResultValue(expectedValue, actualResultParserData.getResult().value());
        if (expectedValue == null) {
            assertNotNull(actualResultParserData.getResult().feedback());
            assertNotNull(actualResultParserData.getResult().feedback().errors());
            assertNotSame(0, actualResultParserData.getResult().feedback().errors().size());
            System.out.println("Expected errors: " + actualResultParserData.getResult().feedback().errors());
        } else {
            if (actualResultParserData.getResult().feedback() != null) {
                assertNull(actualResultParserData.getResult().feedback().errors());
            }
        }
    }

    public static Object[] makeTestData(String srcName, String srcContent, Object expectedValue) {
        ParserData parserData = new ParserData();
        parserData.setSource(new SourceData().name(srcName).pos(0).content(srcContent));

        return new Object[]{parserData, expectedValue};
    }
}
