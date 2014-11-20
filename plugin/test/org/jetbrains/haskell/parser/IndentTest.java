package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;

public class IndentTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public IndentTest() {
        super("indentTests", "hs", new HaskellParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "data";
    }

    @Test
    public void testSimpleIndent() throws Exception { doTest(true); }

    @Test
    public void testBraces() throws Exception { doTest(true); }

    @Test
    public void testClosingBrace() throws Exception { doTest(true); }

    @Test
    public void testLetIn() throws Exception { doTest(true); }

    @Test
    public void testHelloWorld() throws Exception { doTest(true); }

}
