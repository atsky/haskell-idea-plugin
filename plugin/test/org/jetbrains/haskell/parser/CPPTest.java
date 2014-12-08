package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;

public class CPPTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public CPPTest() {
        super("cppTests", "hs", new HaskellParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "data";
    }

    public void testCppIf() throws Exception { doTest(true); }


}
