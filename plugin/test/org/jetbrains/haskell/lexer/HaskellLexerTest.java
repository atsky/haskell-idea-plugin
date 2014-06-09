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
               "COMMENT ('{- {- -} -}')\n" +
               "WHITE_SPACE (' ')");
    }

    @Test
    public void testDigits() throws Exception {
        doTest("0x10FFFF",
               "number ('0x10FFFF')");
    }

    @Test
    public void testStrings() throws Exception {
        doTest("\"\\\\\" ",
                "string ('\"\\\\\"')\n" +
                        "WHITE_SPACE (' ')");
        doTest("'\\x2919'",
               "character (''\\x2919'')");
        doTest("\"\\ \n\t \\\"",
                "string ('\"\\ \\n\t \\\"')");
    }

    @Test
    public void testQuotation() throws Exception {
        doTest("'name",
                "' (''')\n" +
                "id ('name')");
        doTest("''name",
                "'' ('''')\n" +
                "id ('name')");
    }

    @Test
    public void testOperators() throws Exception {
        doTest("\u222F",
                "opertor ('\u222F')");
    }


    @Test
    public void testIndent() throws Exception {
        doTest("module Main where\n" +
                        "\n" +
                        "main\n",
                "module ('module')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "type_cons ('Main')\n" +
                        "WHITE_SPACE (' ')\n" +
                        "where ('where')\n" +
                        "NL ('\\n')\n" +
                        "NL ('\\n')\n" +
                        "VIRTUAL_LEFT_PAREN ('')\n" +
                        "id ('main')\n" +
                        "NL ('\\n')\n" +
                        "VIRTUAL_RIGHT_PAREN ('')");

        doTest("where\n" +
               " { t }",
               "where ('where')\n" +
               "NL ('\\n')\n" +
               "WHITE_SPACE (' ')\n" +
               "{ ('{')\n" +
               "WHITE_SPACE (' ')\n" +
               "id ('t')\n" +
               "WHITE_SPACE (' ')\n" +
               "} ('}')");
    }


}
