package org.flowdev.flowparser.jparsec;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JparsecTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        Terminals operators = Terminals.operators(","); // only one operator supported so far
        Parser<?> integerTokenizer = Terminals.IntegerLiteral.TOKENIZER;
        Parser<String> integerSyntacticParser = Terminals.IntegerLiteral.PARSER;
        Parser<?> ignored = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES);
        Parser<?> tokenizer = Parsers.or(operators.tokenizer(), integerTokenizer); // tokenizes the operators and integer
        Parser<List<String>> integers = integerSyntacticParser.sepBy(operators.token(","))
                .from(tokenizer, ignored.skipMany());
        assertEquals(Arrays.asList("1", "2", "3"), integers.parse("1, /*this is comment*/2, 3"));
    }

}
