package org.jetbrains.haskell.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.cabal.parser.CaballParserDefinition;


public class CabalParsingTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }


    public CabalParsingTest() {
        super("parserTests", "cabal", new CaballParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return ".";
    }

    public void testSimple() throws Exception { doTest(true); }


}
