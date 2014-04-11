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
import org.jetbrains.haskell.parser.rules.lazy


public class HaskellParser(root: IElementType, builder: PsiBuilder) : BaseParser(root, builder) {

    class object {
        private val aFqName = rule(FQ_NAME) {
            notEmptyList(TYPE_OR_CONS, DOT)
        }


    }

    public fun parse(): ASTNode {
        return parseInternal(root)
    }


    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()

        parseModule();

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }



    private val aModuleName = rule(MODULE_NAME) {
        notEmptyList(TYPE_OR_CONS, DOT)
    }

    private val aModuleExports = rule(MODULE_EXPORTS) {
        LEFT_PAREN + aList(anImportElement, COMMA) + maybe(COMMA) + RIGHT_PAREN
    }

    val anImportElement = rule(IMPORT_ELEMENT) {
        ID or
        (TYPE_OR_CONS + maybe(LEFT_PAREN + DOT + DOT + RIGHT_PAREN)) or
        (MODULE_KW + aFqName) or
        (LEFT_PAREN + OPERATOR + RIGHT_PAREN)
    }

    val aImportAsPart = rule(IMPORT_AS_PART) {
        AS_KW + aFqName
    }

    val aFunctionDeclaration = rule(FUNCTION_DECLARATION) {
        ID + COLON + COLON + aType
    }

    val anImport = rule(IMPORT) {

        IMPORT_KW + maybe(QUALIFIED_KW) + aModuleName + maybe(aImportAsPart) +
              maybe(HIDING_KW) + maybe(aModuleExports)
    }

    val aType : Rule = lazy {
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
        val derivingSection = DERIVING_KW + LEFT_PAREN + notEmptyList(TYPE_OR_CONS, COMMA) + RIGHT_PAREN
        DATA_KW + TYPE_OR_CONS + ASSIGNMENT + aList(aConstructor, VERTICAL_BAR) + maybe(derivingSection)
    }


    val aModuleHeader = rule(MODULE_HEADER) {
        (aList(VIRTUAL_SEMICOLON, null) + MODULE_KW + aFqName + maybe(aModuleExports) + WHERE_KW)
    }


    val EXPRESSION : Rule = lazy {
        ID or
        TYPE_OR_CONS or
        (LEFT_BRACKET + aList(EXPRESSION, COMMA) + RIGHT_BRACKET)
    }

    val expressionList = aList(EXPRESSION, null)

    val aFunctionBody = rule(FUNCTION_BODY) {
        ID + expressionList + ASSIGNMENT + EXPRESSION
    }

    fun parseModule() = start(MODULE) {
        val result = aModuleHeader.parse(builder)

        if (result) {
            val rule = VIRTUAL_SEMICOLON or
                       anImport or
                       aDataDeclaration or
                       aFunctionDeclaration or
                       aFunctionBody

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
        while (!builder.eof()) {
            start(HASKELL_TOKEN) {
                builder.advanceLexer()
                true
            }
        }
        true
    }

}