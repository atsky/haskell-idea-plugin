package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;

public class RecoveryTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public RecoveryTest() {
        super("recoveryTests", "hs", new HaskellParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "data";
    }

    public void testRecovery() throws Exception { doTest(true); }

}
