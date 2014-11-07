package org.jetbrains.generator

import java.io.FileReader
import java.util.ArrayList
import org.jetbrains.generator.grammar.TokenDescription
import java.io.PrintStream
import java.io.File
import java.io.FileWriter

/**
 * Created by atsky on 11/7/14.
 */
class Generator {
    fun generate() {
        val lexer = GrammarLexer(FileReader("./grammar/haskell.grm"))

        val grammarParser = GrammarParser(getTokens(lexer))
        val grammar = grammarParser.parseGrammar()!!


        generateTokens(grammar.tokens)
    }

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

    fun generateTokens(tokens: List<TokenDescription>) {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar")
            line("import org.jetbrains.haskell.parser.HaskellToken")
            line()
            line()
            line("object HaslkellTokens {")

            indent {
                for (token in tokens) {
                    val name = token.name.toUpperCase()
                    line("val ${name} = HaskellToken(\"${token.text}\")");
                }
            }
            line("}")
        }


        val parent = File("./grammar/gen/org/jetbrains/grammar/")
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaslkellTokens.kt"))
        writer.write(result.toString())
        writer.close()
    }

}


