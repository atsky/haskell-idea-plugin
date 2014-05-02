package org.jetbrains.haskell.parser.grammar

import org.jetbrains.haskell.parser.rules.RuleBasedElementType
import org.jetbrains.haskell.psi.FqName
import org.jetbrains.haskell.parser.rules.notEmptyList
import org.jetbrains.haskell.parser.token.TYPE_OR_CONS
import org.jetbrains.haskell.parser.token.DOT
import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.lazy
import org.jetbrains.haskell.parser.rules.aList
import org.jetbrains.haskell.parser.aType
import org.jetbrains.haskell.parser.inParentheses
import org.jetbrains.haskell.parser.token.COMMA
import org.jetbrains.haskell.parser.token.DOUBLE_ARROW
import org.jetbrains.haskell.parser.token.ID
import org.jetbrains.haskell.parser.token.EQUALS
import org.jetbrains.haskell.psi.FieldUpdate
import org.jetbrains.haskell.parser.token.LEFT_BRACE
import org.jetbrains.haskell.parser.token.RIGHT_BRACE
import org.jetbrains.haskell.parser.token.LEFT_BRACKET
import org.jetbrains.haskell.parser.token.RIGHT_BRACKET
import org.jetbrains.haskell.psi.CaseClause
import org.jetbrains.haskell.parser.token.RIGHT_ARROW
import org.jetbrains.haskell.parser.token.VIRTUAL_LEFT_PAREN
import org.jetbrains.haskell.parser.token.VIRTUAL_SEMICOLON
import org.jetbrains.haskell.parser.token.VIRTUAL_RIGHT_PAREN
import org.jetbrains.haskell.parser.token.CASE_KW
import org.jetbrains.haskell.parser.token.OF_KW
import com.intellij.lang.PsiBuilder
import org.jetbrains.haskell.psi.DoStatement
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.token.NAME
import org.jetbrains.haskell.parser.token.LEFT_ARROW
import org.jetbrains.haskell.parser.token.LET_KW
import org.jetbrains.haskell.psi.LetExpression
import org.jetbrains.haskell.parser.token.IN_KW
import org.jetbrains.haskell.psi.DoExpression
import org.jetbrains.haskell.parser.token.DO_KW
import org.jetbrains.haskell.parser.token.BACK_SLASH
import org.jetbrains.haskell.psi.ReferenceExpression
import org.jetbrains.haskell.parser.token.UNDERSCORE
import org.jetbrains.haskell.parser.token.COLON
import org.jetbrains.haskell.parser.token.STRING
import org.jetbrains.haskell.parser.token.NUMBER
import org.jetbrains.haskell.parser.token.OPERATOR
import org.jetbrains.haskell.parser.token.DOLLAR
import org.jetbrains.haskell.parser.token.CONSTRUCTOR
import org.jetbrains.haskell.parser.token.VERTICAL_BAR
import org.jetbrains.haskell.parser.token.VALUE_BODY
import org.jetbrains.haskell.psi.ClassDeclaration
import org.jetbrains.haskell.parser.token.CLASS_KW
import org.jetbrains.haskell.parser.rules.maybe
import org.jetbrains.haskell.parser.token.WHERE_KW
import org.jetbrains.haskell.psi.InstanceDeclaration
import org.jetbrains.haskell.parser.token.INSTANCE_KW
import org.jetbrains.haskell.parser.token.MODULE_NAME
import org.jetbrains.haskell.parser.token.MODULE_EXPORTS
import org.jetbrains.haskell.parser.token.LEFT_PAREN
import org.jetbrains.haskell.parser.token.RIGHT_PAREN
import org.jetbrains.haskell.parser.token.SYMBOL_EXPORT
import org.jetbrains.haskell.parser.token.TYPE_KW
import org.jetbrains.haskell.parser.token.DOT_DOT
import org.jetbrains.haskell.parser.token.MODULE_KW
import org.jetbrains.haskell.parser.token.IMPORT_AS_PART
import org.jetbrains.haskell.parser.token.AS_KW
import org.jetbrains.haskell.psi.ValueDeclaration
import org.jetbrains.haskell.parser.token.DOUBLE_COLON
import org.jetbrains.haskell.psi.Import
import org.jetbrains.haskell.parser.token.IMPORT_KW
import org.jetbrains.haskell.parser.token.QUALIFIED_KW
import org.jetbrains.haskell.parser.token.HIDING_KW
import org.jetbrains.haskell.parser.token.CONSTRUCTOR_DECLARATION
import org.jetbrains.haskell.parser.token.DATA_DECLARATION
import org.jetbrains.haskell.parser.token.DERIVING_KW
import org.jetbrains.haskell.parser.token.DATA_KW
import org.jetbrains.haskell.parser.token.NEWTYPE_KW
import org.jetbrains.haskell.parser.SIMPLETYPE
import org.jetbrains.haskell.psi.SomeId
import org.jetbrains.haskell.psi.UnparsedToken
import org.jetbrains.haskell.parser.token.MODULE_HEADER

/**
 * Created by atsky on 5/2/14.
 */
private val FQ_NAME = RuleBasedElementType("FQ name", FqName) {
    notEmptyList(TYPE_OR_CONS, DOT)
}

val CONTEXT : Rule = lazy {
    val aClass : Rule = TYPE_OR_CONS + aList(aType)
    (inParentheses(notEmptyList(aClass, COMMA)) or aClass) + DOUBLE_ARROW
}

val FIELD_BIND: Rule = lazy {
    ID + EQUALS + anExpression
}

val FIELD_UPDATE: Rule = RuleBasedElementType("Field update", FieldUpdate) {
    LEFT_BRACE + notEmptyList(FIELD_BIND, COMMA) + RIGHT_BRACE
}

val anExpression: Rule = lazy {
    aList(anAtomExpression, null)
}

val listLiteral = lazy{
    (LEFT_BRACKET + aList(anExpression, COMMA) + RIGHT_BRACKET)
}

val aCaseCase: Rule = RuleBasedElementType("Case clause", CaseClause) {
    anExpression + RIGHT_ARROW + anExpression
}

val CASE_EXPRESSION = RuleBasedElementType("Case expression", CaseClause) {
    val caseBody = VIRTUAL_LEFT_PAREN + aList(aCaseCase, VIRTUAL_SEMICOLON) + VIRTUAL_RIGHT_PAREN
    CASE_KW + anExpression + OF_KW + caseBody
}

val untilSemicolon : Rule = object : Rule {
    override fun parse(builder: PsiBuilder): Boolean {
        while (builder.getTokenType() != VIRTUAL_SEMICOLON &&
        builder.getTokenType() != VIRTUAL_RIGHT_PAREN &&
        !builder.eof()) {

            (SOME_ID or ANY).parse(builder)
        }
        return true
    }
}

val DO_STATEMENT: Rule = RuleBasedElementType("Do statement", DoStatement) {
    (rule(NAME) { ID } + LEFT_ARROW + anExpression) or
    (LET_KW + ID + EQUALS + anExpression) or
    anExpression or
    untilSemicolon
}


val LET_EXPRESSION = RuleBasedElementType("Let expression", LetExpression) {
    LET_KW + ID + EQUALS + anExpression + IN_KW + anExpression
}

val DO_EXPRESSION = RuleBasedElementType("Do expression", DoExpression) {
    DO_KW + VIRTUAL_LEFT_PAREN + aList(DO_STATEMENT, untilSemicolon + VIRTUAL_SEMICOLON) + VIRTUAL_RIGHT_PAREN
}

val anLambdaLeftPart = lazy {
    BACK_SLASH + notEmptyList(ID) + RIGHT_ARROW
}

val REFERENCE_EXPRESSION = RuleBasedElementType("expression", ReferenceExpression) {
    ID
}

val anAtomExpression = lazy {
    UNDERSCORE or
    COLON or
    STRING or
    NUMBER or
    REFERENCE_EXPRESSION or
    DOT or
    OPERATOR or
    DOLLAR or
    FIELD_UPDATE or
    CASE_EXPRESSION or
    LET_EXPRESSION or
    DO_EXPRESSION or
    anLambdaLeftPart or
    rule(CONSTRUCTOR, { TYPE_OR_CONS }) or
    inParentheses(notEmptyList(anExpression, COMMA)) or
    listLiteral
}

val expressionList = aList(anAtomExpression, null)


val aGuard = lazy {
    VERTICAL_BAR + anExpression + EQUALS + anExpression
}

val aValueBody = rule(VALUE_BODY) {
    val rhs = (EQUALS + anExpression) or notEmptyList(aGuard)
    rule(NAME) {ID} + expressionList + rhs
}

val CLASS_BODY = lazy {
    aList(VALUE_DECLARATION, VIRTUAL_SEMICOLON)
}

val CLASS_DECLARATION = RuleBasedElementType("Class declaration", ClassDeclaration) {
    val body = VIRTUAL_LEFT_PAREN + CLASS_BODY + VIRTUAL_RIGHT_PAREN

    CLASS_KW + maybe(CONTEXT) + TYPE_OR_CONS + aList(aType, null) + WHERE_KW + body
}

val INSTANCE_BODY = lazy {
    aList(aValueBody, VIRTUAL_SEMICOLON)
}

val INSTANCE_DECLARATION = RuleBasedElementType("Instance declaration", InstanceDeclaration) {
    val body = VIRTUAL_LEFT_PAREN + INSTANCE_BODY + VIRTUAL_RIGHT_PAREN

    INSTANCE_KW + maybe(CONTEXT) + TYPE_OR_CONS + aList(aType, null) + WHERE_KW + body
}

private val aModuleName = rule(MODULE_NAME) {
    notEmptyList(TYPE_OR_CONS, DOT)
}

private val aModuleExports = rule(MODULE_EXPORTS) {
    LEFT_PAREN + aList(anExport, COMMA) + maybe(COMMA) + RIGHT_PAREN
}


val anExport = lazy {
    val symbolExport = rule(SYMBOL_EXPORT) {
        ID or TYPE_OR_CONS or inParentheses(OPERATOR)
    }

    val qcnameExt = maybe(TYPE_KW) + symbolExport

    val qcnames = notEmptyList(qcnameExt, COMMA)

    val exportSubspec = inParentheses(maybe((DOT_DOT) or qcnames))

    (qcnameExt + maybe(exportSubspec)) or
    (MODULE_KW + aModuleName)
}

val aImportAsPart = rule(IMPORT_AS_PART) {
    AS_KW + FQ_NAME
}

val VALUE_DECLARATION = RuleBasedElementType("Value declaration", ValueDeclaration) {
    val name = rule(NAME, { ID })
    notEmptyList(name, COMMA) + DOUBLE_COLON + aType
}

val IMPORT = RuleBasedElementType("Import", Import) {
    IMPORT_KW + maybe(QUALIFIED_KW) + aModuleName + maybe(aImportAsPart) +
    maybe(HIDING_KW) + maybe(aModuleExports)
}

val IMPORTS_LIST = aList(IMPORT, VIRTUAL_SEMICOLON)

val typedBinding = lazy {
    rule(NAME, { ID }) + DOUBLE_COLON + aType
}

val extendedConstructor = lazy {
    LEFT_BRACE + aList(typedBinding, COMMA) + RIGHT_BRACE
}

val aConstructor = rule(CONSTRUCTOR_DECLARATION) {
    rule(NAME, { TYPE_OR_CONS }) +
    (extendedConstructor or aList(aType, null))
}

val aDataDeclaration = rule(DATA_DECLARATION) {
    val derivingSection = DERIVING_KW + ((LEFT_PAREN + notEmptyList(TYPE_OR_CONS, COMMA) + RIGHT_PAREN) or TYPE_OR_CONS)
    val data_or_newtype = DATA_KW or NEWTYPE_KW
    data_or_newtype + SIMPLETYPE + EQUALS + aList(aConstructor, VERTICAL_BAR) + maybe(derivingSection)
}

val SOME_ID = RuleBasedElementType("Some id", SomeId) {
    ID or TYPE_OR_CONS or OPERATOR
}

val ANY : Rule = RuleBasedElementType("Any", UnparsedToken) {
    object : Rule {
        override fun parse(builder: PsiBuilder): Boolean {
            builder.advanceLexer()
            return true
        }
    }
}

val aModuleHeader = rule(MODULE_HEADER) {
    (aList(VIRTUAL_SEMICOLON, null) + MODULE_KW + FQ_NAME + maybe(aModuleExports) + WHERE_KW)
}