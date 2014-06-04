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

    @Test
    public void testIndentsComments() throws Exception {
        doTest("module Main where\n" +
               "\n" +
               "main\n",
               "Haskell Token:module ('module')\n" +
               "WHITE_SPACE (' ')\n" +
               "Haskell Token:type_cons ('Main')\n" +
               "WHITE_SPACE (' ')\n" +
               "Haskell Token:where ('where')\n" +
               "NEW_LINE_INDENT ('\\n')\n" +
               "NEW_LINE_INDENT ('\\n')\n" +
               "Haskell Token:VIRTUAL_LEFT_PAREN ('')\n" +
               "Haskell Token:id ('main')\n" +
               "NEW_LINE_INDENT ('\\n')\n" +
               "Haskell Token:VIRTUAL_RIGHT_PAREN ('')");
    }


}
