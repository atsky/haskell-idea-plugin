package org.jetbrains.haskell.parser

import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode

import java.util.ArrayList
import org.jaxen.expr.Expr
import java.util.LinkedList
import java.util.HashMap
import org.jetbrains.haskell.parser.grammar.*
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.lexer.*
import org.jetbrains.haskell.parser.rules.*
import org.jetbrains.haskell.psi.*


public fun inParentheses(rule : Rule) : Rule {
    return LEFT_PAREN + rule + RIGHT_PAREN
}


public class HaskellParser(root: IElementType, builder: PsiBuilder) : BaseParser(root, builder) {

    public fun parse(): ASTNode {
        return parseInternal(root)
    }


    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()
        parseModule();
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }



    fun parseModule() = start(MODULE) {
        val result = (MODULE_HEADER_RULE).parse(builder)

        if (result) {
            val rule = VIRTUAL_SEMICOLON or
                       aDataDeclaration or
                       IMPORT or
                       INSTANCE_DECLARATION or
                       VALUE_DECLARATION or
                       CLASS_DECLARATION or
                       TYPE_DECLARATION or
                       aValueBody

            while (!builder.eof()) {

                if (!rule.parse(builder)) {
                    while (builder.getTokenType() != VIRTUAL_SEMICOLON &&
                           builder.getTokenType() != VIRTUAL_RIGHT_PAREN &&
                           !builder.eof()) {

                        SOME_ID.parse(builder) || start(HASKELL_TOKEN) {
                            builder.advanceLexer()
                            true
                        }
                    }
                    builder.advanceLexer()
                }
            }
        }
        while (!builder.eof()) {
            SOME_ID.parse(builder) || start(HASKELL_TOKEN) {
                builder.advanceLexer()
                true
            }
        }
        true
    }

}