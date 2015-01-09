package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.Constants;

public class CPPTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public CPPTest() {
        super("cppTests", "hs", new HaskellParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return this.getClass().getClassLoader().getResource(Constants.DATA_DIR).getPath();
    }

    public void testCppIf() throws Exception { doTest(true); }


}
