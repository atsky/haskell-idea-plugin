package org.jetbrains.grammar

import java.io.File
import java.io.FileReader
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import org.jetbrains.haskell.parser.rules.ParserState
import com.intellij.lang.PsiBuilder
import com.intellij.lang.impl.PsiBuilderImpl
import java.util.ArrayList
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import org.jetbrains.haskell.parser.token.NEW_LINE
import org.jetbrains.grammar.dumb.GLLParser
import org.jetbrains.haskell.parser.lexer.HaskellIndentLexer
import org.jetbrains.haskell.parser.token.COMMENTS
import org.jetbrains.haskell.parser.token.BLOCK_COMMENT

/**
 * Created by atsky on 15/11/14.
 */
fun main(args : Array<String>) {
    val data = readData()

    val lexer = HaskellIndentLexer()
    lexer.start(data)

    val tokens = ArrayList<IElementType>()

    while (lexer.getTokenType() != null) {
        val tokenType = lexer.getTokenType()
        System.out.print(tokenType)
        if (tokenType != TokenType.WHITE_SPACE &&
            tokenType != NEW_LINE &&
            tokenType != BLOCK_COMMENT) {
            tokens.add(tokenType)
        }
        System.out.print(" ")
        lexer.advance();
    }
    System.out.println()
    System.out.println(tokens)

    val grammar = HaskellParser(null).getGrammar()
    for(rule in grammar) {
        System.out.println(rule)
    }

    GLLParser(grammar, tokens).parse()
}


fun readData() : String {
    val file = File("Test.hs")
    val size = file.length()
    val reader = FileReader(file)
    val charArray = CharArray(size.toInt())
    reader.read(charArray)
    reader.close()
    return String(charArray)
}