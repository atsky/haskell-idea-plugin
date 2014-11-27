package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import org.jetbrains.grammar.dumb.Rule
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.rules.BaseParser
import java.util.ArrayList
import org.jetbrains.grammar.dumb.GLLParser
import org.jetbrains.grammar.dumb.NonTerminalTree

import org.jetbrains.grammar.dumb.TerminalTree

import org.jetbrains.grammar.dumb.Variant
import org.jetbrains.grammar.dumb.Term
import org.jetbrains.grammar.dumb.ll.SimpleLLParser


abstract class BaseHaskellParser(val builder: PsiBuilder?) {

    abstract fun getGrammar() : Map<String, Rule>

    fun mark() : Marker {
        return builder!!.mark()!!
    }

    fun parse(root: IElementType): ASTNode {
        val tokens = ArrayList<IElementType>()

        val marker = builder!!.mark()
        while(builder.getTokenType() != null) {
            tokens.add(builder.getTokenType())
            builder.advanceLexer();
        }
        marker.rollbackTo();

        val rootMarker = mark()

        val tree = SimpleLLParser(getGrammar(), tokens).parse()

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

        val marker = if (type != null) builder!!.mark() else null

        for (child in tree.children) {
            when (child) {
                is NonTerminalTree -> parserWithTree(child)
                is TerminalTree -> {
                    builder!!.advanceLexer()
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

    fun addVar(variants : MutableList<Variant>, vararg terms : Term): Variant {
        val variant = Variant(terms.toArrayList())
        variants.add(variant);
        return variant;
    }
}