package org.jetbrains.grammar

import java.io.File
import java.io.FileReader
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import com.intellij.lang.PsiBuilder
import com.intellij.lang.impl.PsiBuilderImpl
import java.util.ArrayList
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import org.jetbrains.haskell.parser.token.NEW_LINE
import org.jetbrains.haskell.parser.lexer.HaskellIndentLexer
import org.jetbrains.haskell.parser.token.COMMENTS
import org.jetbrains.haskell.parser.token.BLOCK_COMMENT
import org.jetbrains.haskell.parser.token.END_OF_LINE_COMMENT
import java.io.FilenameFilter
import java.io.FileWriter
import java.io.PrintStream
import org.jetbrains.grammar.dumb.SimpleLLParser
import org.jetbrains.haskell.parser.getCachedTokens
import org.jetbrains.haskell.parser.newParserState

/**
 * Created by atsky on 15/11/14.
 */
fun main(args : Array<String>) {
    val path = File("./data/haskellParserTests")
    val filter = object : FilenameFilter {
        override fun accept(dir: File, name: String): Boolean {
            return name.endsWith("HelloWorld.hs")
        }

    }
    for (file in path.listFiles(filter)) {
        val name = file.getName()
        parseFile(file, File(path, name.substring(0, name.size - 3) + "_tree.txt"))
    }
}


fun parseFile(inFile : File, outFile : File) {
    val data = readData(inFile)

    val lexer = HaskellLexer()
    lexer.start(data)

    val stream = PrintStream(outFile)
    val cachedTokens = getCachedTokens(lexer, stream)

    var state = newParserState(cachedTokens)
    while (state.getToken() != null) {
        println(state.getToken());
        state = state.next()
    }

    val grammar = HaskellParser(null).getGrammar()

    HaskellParser(null).findFirst(grammar)

    val parser = SimpleLLParser(grammar, cachedTokens)
    //parser.writeLog = true;
    val start = System.currentTimeMillis()
    //for (i in 1..1000) {
    //    parser.parse()
    //}
    val time = System.currentTimeMillis() - start
    println("time = ${time}")
    val tree = parser.parse()
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