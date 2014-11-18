package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import org.jetbrains.grammar.dumb.Rule
import org.jetbrains.grammar.dumb.GrammarBuilder
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.rules.BaseParser
import java.util.ArrayList
import org.jetbrains.grammar.dumb.GLLParser
import org.jetbrains.grammar.dumb.NonTerminalTree

import org.jetbrains.grammar.dumb.TerminalTree

import org.jetbrains.haskell.parser.grammar.*;
import org.jetbrains.grammar.dumb.Variant
import org.jetbrains.grammar.dumb.Term


abstract class BaseHaskellParser(val builder: PsiBuilder?) {

    val ruleMap = mapOf(
            Pair("module", MODULE),
            Pair("modid", MODULE_NAME),
            Pair("cl_decl", CLASS_DECLARATION),
            Pair("inst_decl", INSTANCE_DECLARATION),
            Pair("sigdecl", VALUE_SIGNATURE),
            Pair("pattern_decl", VALUE_BODY),
            Pair("atype", ATYPE),
            Pair("qvar", QVAR)
    )

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

        val tree = GLLParser(getGrammar(), tokens).parse()

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
        val ruleName = tree.rule
        val type = ruleMap[ruleName]
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
            rule.updateFirst();
        }
    }

    fun addVar(variants : MutableList<Variant>, vararg terms : Term) {
        variants.add(Variant(terms.toArrayList()));
    }

    fun grammar(body : GrammarBuilder.() -> Unit) : Map<String, Rule> {
        val builder = GrammarBuilder()
        builder.body()
        val grammar = builder.rules

        findFirst(grammar);

        return grammar;
    }
}