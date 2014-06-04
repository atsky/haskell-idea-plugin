package org.jetbrains.haskell.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.cabal.parser.CaballParserDefinition;
import org.jetbrains.haskell.parser.lexer.HaskellFullLexer;
import org.junit.Test;


public class HaskellLexerTest extends LexerTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }


    public HaskellLexerTest() {

    }

    @Override
    protected Lexer createLexer() {
        return new HaskellFullLexer();
    }

    @Override
    protected String getDirPath() {
        return "data/haskellLexerTest";
    }

    @Test
    public void testNestedComments() throws Exception {
        doTest(" {- {- -} -} ",
               "WHITE_SPACE (' ')\n" +
               "Haskell Token:COMMENT ('{- {- -} -}')\n" +
               "WHITE_SPACE (' ')");
    }


}
