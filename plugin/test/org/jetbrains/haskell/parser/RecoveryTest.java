package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.Constants;

public class RecoveryTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public RecoveryTest() {
        super("recoveryTests", "hs", new HaskellParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return this.getClass().getClassLoader().getResource(Constants.DATA_DIR).getPath();
    }

    public void testRecovery() throws Exception { doTest(true); }

}
