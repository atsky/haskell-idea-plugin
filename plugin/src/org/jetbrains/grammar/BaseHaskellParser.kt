package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import org.jetbrains.grammar.dumb.Rule
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.rules.BaseParser
import java.util.ArrayList
import org.jetbrains.grammar.dumb.NonTerminalTree

import org.jetbrains.grammar.dumb.TerminalTree

import org.jetbrains.grammar.dumb.Variant
import org.jetbrains.grammar.dumb.Term
import org.jetbrains.grammar.dumb.SimpleLLParser
import org.jetbrains.haskell.parser.getCachedTokens
import org.jetbrains.haskell.parser.token.NEW_LINE
import org.jetbrains.grammar.dumb.Terminal
import org.jetbrains.haskell.parser.HaskellTokenType
import org.jetbrains.grammar.dumb.NonTerminal
import org.jetbrains.grammar.dumb.TerminalVariant
import org.jetbrains.grammar.dumb.NonTerminalVariant


abstract class BaseHaskellParser(val builder: PsiBuilder?) {

    abstract fun getGrammar() : Map<String, Rule>

    fun mark() : Marker {
        return builder!!.mark()!!
    }

    fun parse(root: IElementType): ASTNode {

        val marker = builder!!.mark()
        val cachedTokens = getCachedTokens(builder)
        marker.rollbackTo();

        val rootMarker = mark()

        val tree = SimpleLLParser(getGrammar(), cachedTokens).parse()

        if (tree != null) {
            parserWithTree(tree)
        }

        while (!builder.eof()) {
            builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

    fun parserWithTree(tree: NonTerminalTree) {
        val type = tree.elementType

        val builderNotNull = builder!!
        val marker = if (type != null) builderNotNull.mark() else null

        for (child in tree.children) {
            when (child) {
                is NonTerminalTree -> parserWithTree(child)
                is TerminalTree -> {
                    if (child.haskellToken != HaskellLexerTokens.VOCURLY &&
                        child.haskellToken != HaskellLexerTokens.VCCURLY) {
                        if (child.haskellToken == builderNotNull.getTokenType()) {
                            builderNotNull.advanceLexer()
                        } else if (child.haskellToken != HaskellLexerTokens.SEMI) {
                            throw RuntimeException()
                        }
                    }
                }
            }
        }

        marker?.done(type)
    }

    fun findFirst(grammar : Map<String, Rule>) {
        for (rule in grammar.values()) {
            rule.makeAnalysis(grammar);
        }
    }

    fun term(tokenType : HaskellTokenType): Terminal {
        return Terminal(tokenType)
    }

    fun nonTerm(rule : String): NonTerminal {
        return NonTerminal(rule)
    }

    fun addVar(variants : MutableList<Variant>, vararg terms : Term): TerminalVariant {
        val termsList = terms.toArrayList()
        val last = TerminalVariant()
        var first : Variant = last

        for (term in termsList.reverse()) {
            first = NonTerminalVariant(term, listOf(first))
        }

        variants.add(first);
        return last;
    }
}