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
import org.jetbrains.haskell.parser.token.END_OF_LINE_COMMENT
import java.io.FilenameFilter
import java.io.FileWriter
import java.io.PrintStream

/**
 * Created by atsky on 15/11/14.
 */
fun main(args : Array<String>) {
    val path = File("./data/haskellParserTests")
    val filter = object : FilenameFilter {
        override fun accept(dir: File, name: String): Boolean {
            return name.endsWith(".hs")
        }

    }
    for (file in path.listFiles(filter)) {
        val name = file.getName()
        parseFile(file, File(path, name.substring(0, name.size - 3) + "_tree.txt"))
    }
}


fun parseFile(inFile : File, outFile : File) {
    val data = readData(inFile)

    val lexer = HaskellIndentLexer()
    lexer.start(data)

    val tokens = ArrayList<IElementType>()

    val stream = PrintStream(outFile)
    stream.println("-------------------")
    while (lexer.getTokenType() != null) {
        val tokenType = lexer.getTokenType()
        if (tokenType != TokenType.WHITE_SPACE &&
                tokenType != NEW_LINE &&
                tokenType != END_OF_LINE_COMMENT &&
                tokenType != BLOCK_COMMENT) {
            tokens.add(tokenType)
            stream.print("${tokenType} ")
        }
        if (tokenType == NEW_LINE) {
            stream.println()
        }
        lexer.advance();
    }
    stream.println("\n-------------------")

    val grammar = HaskellParser(null).getGrammar()
    val tree = GLLParser(grammar, tokens).parse()
    stream.println(tree?.prettyPrint(0))
    stream.close()
}

fun readData(file: File): String {
    val size = file.length()
    val reader = FileReader(file)
    val charArray = CharArray(size.toInt())
    reader.read(charArray)
    reader.close()
    return String(charArray)
}