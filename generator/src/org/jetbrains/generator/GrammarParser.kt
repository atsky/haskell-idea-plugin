package org.jetbrains.generator

import org.jetbrains.generator.grammar.Grammar
import org.jetbrains.generator.grammar.TokenDescription
import java.util.ArrayList
import org.jetbrains.generator.grammar.Rule
import org.jetbrains.generator.TokenType.*
import org.jetbrains.generator.grammar.Variant
import org.jetbrains.generator.grammar.RuleRef
import org.jetbrains.generator.grammar.NonFinalVariant
import org.jetbrains.generator.grammar.FinalVariant

/**
 * Created by atsky on 11/7/14.
 */
class GrammarParser(val tokens : List<Token>) {
    var current : Int = -1;

    fun parseGrammar( ) : Grammar? {
        match("token")
        match(OBRACE)
        val tokens = parseTokens()
        match(CBRACE)

        val rules = parseRules()

        return Grammar(tokens, rules);
    }

    fun text(): String {
        return tokens[current].text;
    }

    fun parseTokens( ) : List<TokenDescription> {
        val list = ArrayList<TokenDescription>()

        while (true) {
            if (tryMatch(TokenType.STRING)) {
                val tokenText = text()
                list.add(TokenDescription(tokenText.substring(1, tokenText.length() - 1), getNext()!!.text, true))
            } else if (tryMatch(TokenType.ID)) {
                val tokenText = text()

                list.add(TokenDescription(tokenText, getNext()!!.text, false))
            } else {
                break
            }
        }


        return list;
    }

    fun match(text: String) {
        getNext()
        if (text() == text) {
            throw RuntimeException()
        }
    }

    fun match(expected: TokenType) : Token {
        val next = getNext()
        if (next == null || next.type != expected) {
            throw ParserException(next, "${expected} expected, but ${next?.type}");
        }
        return next;
    }

    fun tryMatch(type: TokenType): Boolean {
        if (getNext()!!.type != type) {
            current--;
            return false
        }
        return true;
    }

    fun getNext(): Token? {
        if (current < tokens.size()) {
            current++
            return if (current < tokens.size()) tokens[current] else null;
        } else {
            return null;
        }
    }

    fun parseRules() : List<Rule> {
        val list = ArrayList<Rule>()
        while (!eof()) {
            list.add(parseRule())
        }
        return list;
    }

    fun parseRule() : Rule {
        val name = match(ID).text
        match(COLON)

        val variants = ArrayList<Variant>()

        while (true) {
            variants.add(parseVariant())
            if (!tryMatch(VBAR)) {
                break;
            }
        }

        match(SEMICOLON)
        return Rule(name, variants)
    }


    fun eof(): Boolean {
        return current >= tokens.size - 1;
    }

    fun parseVariant() : Variant {
        val list = ArrayList<RuleRef>()
        while (true) {
            if (tryMatch(TokenType.STRING)) {
                val t = text()
                list.add(RuleRef(t.substring(1, t.length - 1), false))
            } else if (tryMatch(TokenType.ID)) {
                list.add(RuleRef(text(), true))
            } else {
                break
            }
        }
        val name = if (tryMatch(OBRACE)) {
            match(TokenType.ID)
            val text = text()
            match(CBRACE)
            text
        } else {
            null
        }

        var variant : Variant = FinalVariant(name);

        for (ref in list.reversed()) {
            variant = NonFinalVariant(ref, listOf(variant))
        }

        return variant;
    }
}