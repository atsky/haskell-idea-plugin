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


public fun inParentheses(rule: Rule): Rule {
    return LEFT_PAREN + rule + RIGHT_PAREN
}


public class HaskellParser(root: IElementType, builder: PsiBuilder) : BaseParser(root, builder) {

    val state = ParserState(builder)

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
        (MODULE_HEADER_RULE).parse(state)


        val rule = VIRTUAL_SEMICOLON or
        aDataDeclaration or
        IMPORT or
        INSTANCE_DECLARATION or
        VALUE_DECLARATION or
        CLASS_DECLARATION or
        TYPE_DECLARATION or
        aValueBody

        while (!state.eof()) {

            if (!rule.parse(state)) {
                while (state.getTokenType() != VIRTUAL_SEMICOLON &&
                state.getTokenType() != VIRTUAL_RIGHT_PAREN &&
                !state.eof()) {

                    SOME_ID.parse(state) || start(HASKELL_TOKEN) {
                        state.advanceLexer()
                        true
                    }
                }
                state.advanceLexer()
            }
        }

        while (!state.eof()) {
            SOME_ID.parse(state) || start(HASKELL_TOKEN) {
                state.advanceLexer()
                true
            }
        }
        true
    }

}