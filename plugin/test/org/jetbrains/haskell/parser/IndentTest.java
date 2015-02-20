package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;

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

    public void testBraces() throws Exception { doTest(true); }

    public void testBracesIndent() throws Exception { doTest(true); }

    public void testClosingBrace() throws Exception { doTest(true); }

    public void testHelloWorld() throws Exception { doTest(true); }

    public void testIndentInParenthesis() throws Exception { doTest(true); }

    public void testIndentInBraces() throws Exception { doTest(true); }

    public void testSimpleIndent() throws Exception { doTest(true); }

    public void testLetIn() throws Exception { doTest(true); }

    public void testTabs() throws Exception { doTest(true); }

    public void testCaseInList() throws Exception { doTest(true); }

    public void testLetInSameIndent() throws Exception { doTest(true); }

    public void testTwoClosingBraces() throws Exception { doTest(true); }

}
