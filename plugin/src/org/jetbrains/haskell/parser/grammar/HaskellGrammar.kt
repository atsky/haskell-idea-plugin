package org.jetbrains.haskell.parser.grammar

import org.jetbrains.haskell.parser.rules.RuleBasedElementType
import org.jetbrains.haskell.parser.rules.notEmptyList
import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.lazy
import org.jetbrains.haskell.parser.rules.aList
import org.jetbrains.haskell.parser.inParentheses
import org.jetbrains.haskell.parser.token.*
import com.intellij.lang.PsiBuilder
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.rules.maybe
import org.jetbrains.haskell.psi.*
import org.jetbrains.haskell.parser.grammar.*
import org.jetbrains.haskell.parser.rules.ParserState

/**
 * Created by atsky on 5/2/14.
 */
private val FQ_NAME = RuleBasedElementType("FQ name", ::FqName) {
    notEmptyList(TYPE_OR_CONS, DOT)
}


private val VALUE_NAME = RuleBasedElementType("Value name", ::ValueName) {
    simpleId
}

val CONTEXT : Rule = lazy {
    val aClass : Rule = TYPE_OR_CONS + aList(TYPE)
    (inParentheses(notEmptyList(aClass, COMMA)) or aClass) + DOUBLE_ARROW
}

val untilSemicolon : Rule = object : Rule {
    override fun parse(state: ParserState): Boolean {
        while (state.getTokenType() != VIRTUAL_SEMICOLON &&
               state.getTokenType() != VIRTUAL_RIGHT_PAREN &&
               !state.eof()) {

            (SOME_ID or ANY).parse(state)
        }
        return true
    }
}

val SEMICOLON_RULE = (SEMICOLON or VIRTUAL_SEMICOLON)

val RIGHT_BRACE_RULE = object : Rule {

    override fun parse(state: ParserState): Boolean {
        val tokenType = state.getTokenType()
        if (tokenType == VIRTUAL_RIGHT_PAREN) {
            state.advanceLexer()
        } else {
            state.popIndent()
        }

        return true;
    }

}

fun inBraces(rule : Rule) : Rule =
    (LEFT_BRACE + rule + RIGHT_BRACE) or (VIRTUAL_LEFT_PAREN + rule + RIGHT_BRACE_RULE)


val expressionList = aList(anAtomExpression, null)


val aGuard = lazy {
    VERTICAL_BAR + anExpression + EQUALS + anExpression
}

val aValueBody = rule(VALUE_BODY) {
    val rhs = (EQUALS + anExpression) or notEmptyList(aGuard)
    VALUE_NAME + expressionList + rhs
}

val CLASS_BODY = lazy {
    aList(VALUE_DECLARATION, VIRTUAL_SEMICOLON)
}

val CLASS_DECLARATION = RuleBasedElementType("Class declaration", ::ClassDeclaration) {
    val body = VIRTUAL_LEFT_PAREN + CLASS_BODY + VIRTUAL_RIGHT_PAREN

    CLASS_KW + maybe(CONTEXT) + TYPE_OR_CONS + aList(TYPE, null) + WHERE_KW + body
}

val INSTANCE_BODY = lazy {
    aList(aValueBody, VIRTUAL_SEMICOLON)
}

val INSTANCE_DECLARATION = RuleBasedElementType("Instance declaration", ::InstanceDeclaration) {
    val body = VIRTUAL_LEFT_PAREN + INSTANCE_BODY + VIRTUAL_RIGHT_PAREN

    INSTANCE_KW + maybe(CONTEXT) + TYPE_OR_CONS + aList(TYPE, null) + WHERE_KW + body
}

private val aModuleName = rule(MODULE_NAME) {
    notEmptyList(TYPE_OR_CONS, DOT)
}

private val aModuleExports = rule(MODULE_EXPORTS) {
    LEFT_PAREN + aList(anExport, COMMA) + maybe(COMMA) + maybe(VIRTUAL_SEMICOLON) + RIGHT_PAREN
}

val modulePrefix = RuleBasedElementType("ModulePrefix", ::ModulePrefix) {
    notEmptyList(TYPE_OR_CONS + DOT)
}

val anExport = lazy {

    val symbolExport = rule(SYMBOL_EXPORT) {
        simpleId or TYPE_OR_CONS or inParentheses(OPERATOR_ID)
    }

    val qcnameExt = maybe(TYPE_KW) + maybe(modulePrefix) + symbolExport

    val qcnames = notEmptyList(qcnameExt, COMMA)

    val exportSubspec = inParentheses(maybe((DOT_DOT) or qcnames))

    (qcnameExt + maybe(exportSubspec)) or (MODULE_KW + aModuleName)
}

val aImportAsPart = rule(IMPORT_AS_PART) {
    AS_KW + FQ_NAME
}

val VALUE_DECLARATION = RuleBasedElementType("Value declaration", ::ValueDeclaration) {
    notEmptyList(VALUE_NAME, COMMA) + DOUBLE_COLON + TYPE
}

val IMPORT = RuleBasedElementType("Import", ::Import) {
    IMPORT_KW + maybe(QUALIFIED_KW) + aModuleName + maybe(aImportAsPart) +
    maybe(HIDING_KW) + maybe(aModuleExports)
}

val IMPORTS_LIST = aList(IMPORT, VIRTUAL_SEMICOLON)

val typedBinding = lazy {
    VALUE_NAME + DOUBLE_COLON + TYPE
}

val extendedConstructor = lazy {
    LEFT_BRACE + aList(typedBinding, COMMA) + RIGHT_BRACE
}

val aConstructorName = RuleBasedElementType("ConstructorName", ::ConstructorName) {
    TYPE_OR_CONS
}


val aConstructor = rule(CONSTRUCTOR_DECLARATION) {
    aConstructorName + (extendedConstructor or aList(TYPE))
}

val aDataDeclaration = rule(DATA_DECLARATION) {
    val derivingSection = DERIVING_KW + ((LEFT_PAREN + notEmptyList(TYPE_OR_CONS, COMMA) + RIGHT_PAREN) or TYPE_OR_CONS)
    val data_or_newtype = DATA_KW or NEWTYPE_KW
    data_or_newtype + SIMPLETYPE + EQUALS + aList(aConstructor, VERTICAL_BAR) + maybe(derivingSection)
}

val SOME_ID = RuleBasedElementType("Some id", ::SomeId) {
    simpleId or TYPE_OR_CONS or OPERATOR_ID
}

val ANY : Rule = RuleBasedElementType("Any", ::UnparsedToken) {
    object : Rule {
        override fun parse(state: ParserState): Boolean {
            state.advanceLexer()
            return true
        }
    }
}

val MODULE_HEADER_RULE = rule(MODULE_HEADER) {
    (aList(PRAGMA) + MODULE_KW + FQ_NAME + maybe(aModuleExports) + WHERE_KW + VIRTUAL_LEFT_PAREN)
}