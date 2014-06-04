package org.jetbrains.cabal.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;

public class CabalParsingTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public CabalParsingTest() {
        super("cabalParserTests", "cabal", new CaballParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "data";
    }

    @Test
    public void testSimple() throws Exception { doTest(true); }

}
