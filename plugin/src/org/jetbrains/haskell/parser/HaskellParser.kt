package org.jetbrains.haskell.parser

import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode

import org.jetbrains.haskell.util.ProcessRunner
import org.jetbrains.haskell.util.lisp.LispParser
import org.jetbrains.haskell.util.lisp.SList
import org.jetbrains.haskell.util.lisp.SExpression
import java.util.ArrayList
import org.jaxen.expr.Expr
import java.util.LinkedList
import java.util.HashMap
import org.jetbrains.haskell.util.lisp.SAtom
import org.jetbrains.haskell.compiler.GHCInterface
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.lexer.*
import org.jetbrains.haskell.parser.rules.BaseParser
import org.jetbrains.haskell.parser.rules.notEmptyList
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.rules.aList


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



    fun parseFqName() = start(FQ_NAME) {
        notEmptyList(TYPE_OR_CONS, DOT).parse(builder)
    }

    fun parseModuleName() = start(MODULE_NAME) {
        token(TYPE_OR_CONS) && zeroOrMore {
            token(DOT) && token(TYPE_OR_CONS)
        }
    }

    fun importElement() = start(IMPORT_ELEMENT) {
        token(ID) || (token(TYPE_OR_CONS) && maybe(atom {
                token(LEFT_PAREN) && token(DOT) && token(DOT) && token(RIGHT_PAREN)
        }))
    }

    fun commaSeparatedImportList() = maybe(atom {
        importElement() && zeroOrMore {
            token(COMMA) && importElement()
        }
    })

    fun parseImportAsPart() = start(IMPORT_AS_PART) {
        token(AS_KEYWORD) && parseFqName()
    }

    fun parseFunctionDeclaration() = start(FUNCTION_DECLARATION) {
        token(ID) && token(COLON) && token(COLON)
    }

    fun parseImport() = start(IMPORT) {
        val result = token(IMPORT_KEYWORD) && maybe(token(QUALIFIED_KEYWORD)) && parseModuleName()

        parseImportAsPart()

        atom {
            token(HIDING_KEYWORD)
            token(LEFT_PAREN) && commaSeparatedImportList() && token(RIGHT_PAREN)
        }
        result
    }

    val aType = rule(TYPE, TYPE_OR_CONS)

    val aConstructor = rule(CONSTRUCTOR_DECLARATION,
        TYPE_OR_CONS and aList(aType, null))

    fun parseDataDeclaration() = start(DATA_DECLARATION) {
        (DATA_KEYWORD and TYPE_OR_CONS and ASSIGNMENT).parse(builder)
    }


    fun parseModule() = start(MODULE) {
        val result = token(MODULE_KEYWORD) && parseFqName() && token(WHERE_KEYWORD)


        result && zeroOrMore {
            token(VIRTUAL_SEMICOLON) ||
            parseImport() ||
            parseDataDeclaration() ||
            parseFunctionDeclaration()
        }

        while (!builder.eof()) {
            start(HASKELL_TOKEN) {
                builder.advanceLexer()
                true
            }
        }

        true
    }

}