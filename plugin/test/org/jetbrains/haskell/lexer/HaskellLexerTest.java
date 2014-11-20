package org.jetbrains.haskell.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import org.jetbrains.haskell.parser.lexer.HaskellLexer;
import org.junit.Test;


public class HaskellLexerTest extends LexerTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }


    public HaskellLexerTest() {

    }

    @Override
    protected Lexer createLexer() {
        return new HaskellLexer();
    }

    @Override
    protected String getDirPath() {
        return "data/haskellLexerTest";
    }

    public void testNestedComments() throws Exception {
        doTest(" {- {- -} -} ",
               "WHITE_SPACE (' ')\n" +
               "COMMENT ('{- {- -} -}')\n" +
               "WHITE_SPACE (' ')");
    }

    public void testDigits() throws Exception {
        doTest("0x10FFFF",
               "INTEGER ('0x10FFFF')");
    }

    public void testStrings() throws Exception {
        doTest("\"\\\\\" ",
                "STRING ('\"\\\\\"')\n" +
                        "WHITE_SPACE (' ')");
        doTest("'\\x2919'",
               "CHAR (''\\x2919'')");
        doTest("\"\\ \n\t \\\"",
                "STRING ('\"\\ \\n\t \\\"')");
    }

    public void testQuotation() throws Exception {
        doTest("'name",
                "' (''')\n" +
                "VARID ('name')");
        doTest("''name",
                "'' ('''')\n" +
                "VARID ('name')");
    }

    public void testOperators() throws Exception {
        doTest("\u222F",
                "VARSYM ('\u222F')");
    }

}
