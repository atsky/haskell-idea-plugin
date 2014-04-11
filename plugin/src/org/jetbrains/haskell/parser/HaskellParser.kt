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
import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.maybe


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

    private val aFqName = rule(FQ_NAME) {
        notEmptyList(TYPE_OR_CONS, DOT)
    }

    private val aModuleName = rule(MODULE_NAME) {
        notEmptyList(TYPE_OR_CONS, DOT)
    }

    val anImportElement = rule(IMPORT_ELEMENT) {
        ID or (TYPE_OR_CONS + maybe(LEFT_PAREN + DOT + DOT + RIGHT_PAREN))
    }

    val aImportAsPart = rule(IMPORT_AS_PART) {
        AS_KEYWORD + aFqName
    }

    val aFunctionDeclaration = rule(FUNCTION_DECLARATION) {
        ID + COLON + COLON + aType
    }

    val parseImport = rule(IMPORT) {
        val importList = LEFT_PAREN + aList(anImportElement, COMMA) + RIGHT_PAREN

        IMPORT_KEYWORD + maybe(QUALIFIED_KEYWORD) + aModuleName + maybe(aImportAsPart) +
              maybe(HIDING_KEYWORD) + maybe(importList)
    }

    val aType : Rule = rule(TYPE) {
        aArrowType or aApplicationType
    }

    private val aArrowType : Rule = rule(ARROW_TYPE) {
        aApplicationType + ARROW + aType
    }

    val aApplicationType : Rule = rule(APPLICATION_TYPE) {
        (aPrimitiveType + aApplicationType) or aPrimitiveType
    }

    val aPrimitiveType : Rule = rule(TYPE) {
        TYPE_OR_CONS or
        (LEFT_BRACKET + aType + RIGHT_BRACKET) or
        (LEFT_PAREN + RIGHT_PAREN)
    }

    val aConstructor = rule(CONSTRUCTOR_DECLARATION) {
        TYPE_OR_CONS + aList(aType, null)
    }

    val aDataDeclaration = rule(DATA_DECLARATION) {
        DATA_KEYWORD + TYPE_OR_CONS + ASSIGNMENT + aList(aConstructor, VERTICAL_BAR)
    }


    fun parseModule() = start(MODULE) {
        val result = (aList(VIRTUAL_SEMICOLON, null) + MODULE_KEYWORD + aFqName + WHERE_KEYWORD).parse(builder)

        if (result) {
            val rule = VIRTUAL_SEMICOLON or parseImport or aDataDeclaration or aFunctionDeclaration

            while (!builder.eof()) {

                if (!rule.parse(builder)) {
                    while (builder.getTokenType() != VIRTUAL_SEMICOLON &&
                           builder.getTokenType() != VIRTUAL_RIGHT_PAREN &&
                           !builder.eof()) {

                        start(HASKELL_TOKEN) {
                            builder.advanceLexer()
                            true
                        }
                    }
                    builder.advanceLexer()
                }
            }
        }
        true
    }

}