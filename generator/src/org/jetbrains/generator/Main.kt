package org.jetbrains.generator

import java.io.FileReader
import java.util.ArrayList

/**
 * Created by atsky on 11/7/14.
 */

fun getTokens(lexer : GrammarLexer) : List<Token> {
    val list = ArrayList<Token>();
    while (true) {
        val tokenType = lexer.yylex()
        if (tokenType == null) {
            break
        }
        if (tokenType == TokenType.BLOCK_COMMENT) {
            continue
        }
        if (tokenType == TokenType.EOL_COMMENT) {
            continue
        }
        list.add(Token(tokenType, lexer.yytext()))
    }
    return list;
}

fun main(args : Array<String>) {

    val lexer = GrammarLexer(FileReader("./plugin/haskell.grm"))

    val grammarParser = GrammarParser(getTokens(lexer))
     val grammar = grammarParser.parseGrammar()!!
    val generator = ParserGenerator(grammar)
    generator.generate()
}