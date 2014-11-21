package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;

public class HaskellTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public HaskellTest() {
        super("haskellParserTests", "hs", new HaskellParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "data";
    }

    public void testDataType() throws Exception { doTest(true); }

    public void testRecovery() throws Exception { doTest(true); }

    public void testImports() throws Exception { doTest(true); }

    public void testHelloWorld() throws Exception { doTest(true); }

    public void testMaximum() throws Exception { doTest(true); }

}
