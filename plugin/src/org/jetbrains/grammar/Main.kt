package org.jetbrains.grammar

import java.io.File
import java.io.FileReader
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import org.jetbrains.haskell.parser.rules.ParserState
import com.intellij.lang.PsiBuilder
import com.intellij.lang.impl.PsiBuilderImpl

/**
 * Created by atsky on 15/11/14.
 */
fun main(args : Array<String>) {
    val data = readData()

    val lexer = HaskellLexer()
    lexer.start(data)

    while (lexer.getTokenType() != null) {
        System.out.print(lexer.getTokenType())
        System.out.print(" ")
        lexer.advance();
    }
    System.out.println()

    val grammar = HaskellParser(null).getGrammar()
    for(rule in grammar) {
        System.out.println(rule)
    }

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