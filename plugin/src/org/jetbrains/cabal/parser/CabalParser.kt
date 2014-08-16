package org.jetbrains.cabal.parser

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.haskell.parser.rules.BaseParser
import org.jetbrains.haskell.parser.HaskellToken
import com.siyeh.ig.dataflow.BooleanVariableAlwaysNegatedInspectionBase


public class CabalParser(root: IElementType, builder: PsiBuilder) : BaseParser(root, builder) {

    public fun parse(): ASTNode = parseInternal(root)

    fun canParse(parse: () -> Boolean): Boolean {
        val marker = builder.mark()!!
        val res = parse()
        marker.rollbackTo()
        return res
    }

    fun indentSize(str: String): Int {
        val indexOf = str.lastIndexOf('\n')
        return str.size - indexOf - 1
    }

    fun nextLevel() : Int? {                                  //there can never be two NEW_LINE_INDENT's next to each other
        if ((!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT)) {
            return indentSize(builder.getTokenText()!!)
        }
        return null
    }

    fun isLastOnThisLine() : Boolean = builder.eof() || (builder.getTokenType() == TokenType.NEW_LINE_INDENT)

    fun skipNewLineBiggerLevel(prevLevel: Int) {
        val nextIndent = nextLevel()
        if ((nextIndent != null) && (nextIndent > prevLevel))
            builder.advanceLexer()
    }

    fun isLastBiggerLevel(level: Int) : Boolean {
        val nextIndent = nextLevel()
        if (builder.eof() || ((nextIndent != null) && (nextIndent <= level))) {
            return true
        }
        return false
    }

    fun skipAllBiggerLevelTill(level: Int, parseSeparator: () -> Boolean) {
        while (!builder.eof()) {
            if (isLastBiggerLevel(level)) {
                break
            }
            if (canParse({ skipNewLineBiggerLevel(level); parseSeparator() })) {
                break
            }
            builder.advanceLexer();
        }
    }

    fun skipFreeLineTill(parseSeparator: () -> Boolean) {
        while (!isLastOnThisLine() && !canParse(parseSeparator)) {
            builder.advanceLexer();
        }
    }

    fun parsePropertyKey(propName : String?) = start(CabalTokelTypes.PROPERTY_KEY) {
        if (propName == null) token(CabalTokelTypes.ID) else matchesIgnoreCase(CabalTokelTypes.ID, propName)
    }

    fun parseBool() = matchesIgnoreCase(CabalTokelTypes.ID, "true") || matchesIgnoreCase(CabalTokelTypes.ID, "false")

    fun parseVersion() = token(CabalTokelTypes.NUMBER) || token(CabalTokelTypes.ID)

    fun parseSimpleVersionConstraint() = start(CabalTokelTypes.VERSION_CONSTRAINT) {
        if (token(CabalTokelTypes.COMPARATOR)) {
            parseVersion()
        }
        else {
            matches(CabalTokelTypes.ID, "-any")
        }
    }

    fun parseFreeLine(elemType: IElementType) = start(elemType) {
        var isEmpty = true
        while (!isLastOnThisLine()) {
            builder.advanceLexer()
            isEmpty = false
        }
        !isEmpty
    }

    fun parseInvalidLine() = parseFreeLine(CabalTokelTypes.INVALID_VALUE) || start(CabalTokelTypes.INVALID_VALUE, { true })

    fun parseAsInvalid(parseBody: () -> Boolean) = start(CabalTokelTypes.INVALID_VALUE, { parseBody() })

    fun parseFreeForm(prevLevel: Int) = start(CabalTokelTypes.FREE_FORM) {
        skipAllBiggerLevelTill(prevLevel, parseSeparator = { false }); true
    }

    fun parseValueTillSeparator(prevLevel: Int, parseSeparator: () -> Boolean, onOneLine: Boolean) = start(CabalTokelTypes.INVALID_VALUE) {
        if (!onOneLine) {
            skipAllBiggerLevelTill(prevLevel, parseSeparator)
        }
        else {
            skipFreeLineTill(parseSeparator)
        }
        true
    }

    fun parseIdValue(elemType: IElementType) = start(elemType, { token(CabalTokelTypes.ID) })

    fun parseTokenValue(elemType: IElementType) = start(elemType) {

        fun nextTokenIsValid() = !isLastOnThisLine()
                              && (builder.getTokenType() != CabalTokelTypes.COMMA)
                              && (builder.getTokenType() != CabalTokelTypes.TAB)

        fun emptySpaceBeforeNext()
                = (builder.rawTokenTypeStart(1) != builder.getCurrentOffset() + builder.getTokenText()!!.size)

        var isEmpty = true
        while (nextTokenIsValid()) {
            builder.advanceLexer()
            isEmpty = false
            if (!isLastOnThisLine() && emptySpaceBeforeNext()) break
        }
        !isEmpty
    }

    fun parsePath() = parseTokenValue(CabalTokelTypes.PATH)

    fun parseVersionValue() = start(CabalTokelTypes.VERSION_VALUE, { parseVersion() })

    fun parseTillSeparatorOrPrevLevel(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean, separatorIsOptional: Boolean) : Boolean {
        if (!onOneLine) skipNewLineBiggerLevel(prevLevel)                                                          // returns false if there is nothing to parse
        val mark = builder.mark()!!
        var valueParsed = parseValue()
        if (!onOneLine) skipNewLineBiggerLevel(prevLevel)
        if (valueParsed && (isLastBiggerLevel(prevLevel) || canParse({ parseSeparator() }) || separatorIsOptional)) {
            mark.drop()
        }
        else {
            mark.rollbackTo()
            parseValueTillSeparator(prevLevel, parseSeparator, onOneLine)
            if (!onOneLine) skipNewLineBiggerLevel(prevLevel)
        }
        return true
    }

    fun parseTillEndValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean, separatorIsOptional: Boolean) : Boolean {
        do {
            parseTillSeparatorOrPrevLevel(prevLevel, parseValue, parseSeparator, onOneLine, separatorIsOptional)
        } while ((!builder.eof()) && !isLastBiggerLevel(prevLevel) && (parseSeparator() || separatorIsOptional))
        return true
    }

    fun parseTillValidValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean) : Boolean {
        var mark: Marker? = builder.mark()!!
        var nonEmpty = false
        do {
            if (!onOneLine) skipNewLineBiggerLevel(prevLevel);
            if (parseValue()) {
                mark?.drop()
                nonEmpty = true
            }
            else break
            mark = builder.mark()!!
            if (!onOneLine) skipNewLineBiggerLevel(prevLevel);
        } while ((!builder.eof()) && parseSeparator())
        mark?.rollbackTo()
        return nonEmpty
    }

    fun parseCommonCommaList(prevLevel: Int, parseBody: () -> Boolean) = parseTillEndValueList(
            prevLevel,
            parseBody,
            { token(CabalTokelTypes.COMMA) },
            onOneLine = false,
            separatorIsOptional = true
    )

    fun parseTokenList(prevLevel: Int)  = parseCommonCommaList(prevLevel, { parseTokenValue(CabalTokelTypes.TOKEN) })

    fun parseIdList(prevLevel: Int) = parseCommonCommaList(prevLevel, { parseIdValue(CabalTokelTypes.IDENTIFIER) })

    fun parseOptionList(prevLevel: Int) = parseCommonCommaList(prevLevel, { parseTokenValue(CabalTokelTypes.OPTION) })

    fun parsePathList(prevLevel: Int)   = parseCommonCommaList(prevLevel, { parsePath() })

    fun parseLanguageList(prevLevel: Int) = parseCommonCommaList(prevLevel, { parseIdValue(CabalTokelTypes.LANGUAGE) })

    fun parseComplexVersionConstraint(prevLevel : Int, onOneLine: Boolean = false) = start(CabalTokelTypes.COMPLEX_CONSTRAINT) {
        parseTillValidValueList(prevLevel, { parseSimpleVersionConstraint() }, { token(CabalTokelTypes.LOGIC) }, onOneLine)
    }

    fun parseFullVersionConstraint(prevLevel: Int, tokenType: IElementType, onOneLine: Boolean = false) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        parseIdValue(tokenType)
                && (parseComplexVersionConstraint(prevLevel, onOneLine) || true)
    }

    fun parseSimpleCondition(prevLevel: Int) = start(CabalTokelTypes.SIMPLE_CONDITION) {
        val testName = builder.getTokenText()
        if (parseBool()) {
            true
        }
        else if (token(CabalTokelTypes.ID) && token(CabalTokelTypes.OPEN_PAREN)) {
            var res: Boolean
            when (testName) {
                "impl" -> res = parseFullVersionConstraint(prevLevel, CabalTokelTypes.COMPILER, true)
                "flag" -> res = parseIdValue(CabalTokelTypes.NAME)
                else   -> res = parseIdValue(CabalTokelTypes.IDENTIFIER)
            }
            res && token(CabalTokelTypes.CLOSE_PAREN)
        }
        else false
    }

    fun parseInvalidConditionPart() = start(CabalTokelTypes.INVALID_CONDITION_PART) {
        while (!builder.eof() && (builder.getTokenType() != CabalTokelTypes.LOGIC)
                                        && (builder.getTokenType() != CabalTokelTypes.CLOSE_PAREN)
                                        && (builder.getTokenType() != TokenType.NEW_LINE_INDENT)) {
            if ((builder.getTokenText() == "flag") && token(CabalTokelTypes.ID) && token(CabalTokelTypes.OPEN_PAREN)) {
                parseIdValue(CabalTokelTypes.NAME)
            }
            else builder.advanceLexer()
        }
        true
    }

    fun parseConditionPart(prevLevel: Int): Boolean = start(CabalTokelTypes.CONDITION_PART) {
        if (token(CabalTokelTypes.NEGATION)) {
            parseConditionPart(prevLevel)
        }
        else if (token(CabalTokelTypes.OPEN_PAREN)) {
            parseCondition(prevLevel) && (token(CabalTokelTypes.CLOSE_PAREN) || true)
        }
        else {
            parseSimpleCondition(prevLevel)
        }
    }

    fun parseCondition(prevLevel: Int) = parseTillValidValueList(
            prevLevel,
            { parseConditionPart(prevLevel) || parseInvalidConditionPart() },
            { token(CabalTokelTypes.LOGIC) || (parseInvalidConditionPart() && token(CabalTokelTypes.LOGIC)) },
            onOneLine = true
    )

    fun parseFullCondition(level: Int) = start(CabalTokelTypes.FULL_CONDITION, { parseCondition(level) })

    fun parseConstraintList(prevLevel: Int, tokenType: IElementType = CabalTokelTypes.IDENTIFIER) = parseTillEndValueList(
            prevLevel,
            { parseFullVersionConstraint(prevLevel, tokenType) },
            { token(CabalTokelTypes.COMMA) },
            onOneLine = false,
            separatorIsOptional = false
    )

    fun parseCompilerList(prevLevel: Int) = parseConstraintList(prevLevel, CabalTokelTypes.COMPILER)

    fun parseField(level: Int, key : String?, elemType: IElementType, parseValue : CabalParser.(Int) -> Boolean) = start(elemType) {
        if (parsePropertyKey(key) && token(CabalTokelTypes.COLON)) {
            skipNewLineBiggerLevel(level)

            (parseValue(level) && isLastBiggerLevel(level))
                    || parseValueTillSeparator(level, parseSeparator = { false }, onOneLine = false)
        }
        else false
    }

    fun parseInvalidField(level: Int) = start(CabalTokelTypes.INVALID_FIELD) {
        if (parseIdValue(CabalTokelTypes.NAME)) {
            token(CabalTokelTypes.COLON)
            skipAllBiggerLevelTill(level, parseSeparator = { false })
            true
        }
        else false
    }

    fun parseFieldFrom(level: Int, fields: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>>) : Boolean {
        for (key in fields.keySet()) {
            if (parseField(level, key, fields.get(key)!!.first, fields.get(key)!!.second)) return true
        }
        return false
    }

    fun parseProperties(prevLevel: Int, parseFields: CabalParser.(Int) -> Boolean, canContainIf: Boolean): Boolean {

        fun parseSomeField(level: Int) = parseFields(level)
                                      || (canContainIf && parseIfElse(level, parseFields))
                                      || parseInvalidField(level)
                                      || parseInvalidLine()

        var currentLevel : Int? = null
        while (!builder.eof()) {
            val level = nextLevel()
            if (level == null) return false
            if (((currentLevel == null) || (level != currentLevel!!)) && (level <= prevLevel)) {
                return true                                                                       //sections without any field is allowed
            }
            else if ((currentLevel == null) && (level > prevLevel)) {
                currentLevel = level
            }
            skipNewLineBiggerLevel(prevLevel)
            if ((currentLevel != null) && (level != currentLevel!!) && (level > prevLevel)) {
                parseValueTillSeparator(currentLevel!!, parseSeparator = { false }, onOneLine = false)
            }
            else {
                parseSomeField(level)
            }
        }
        return true
    }

    fun parseSectionType(name: String) = start(CabalTokelTypes.SECTION_TYPE) {
        matchesIgnoreCase(CabalTokelTypes.ID, name)
    }

    fun parseRepoKinds() = (parseIdValue(CabalTokelTypes.REPO_KIND) && parseIdValue(CabalTokelTypes.REPO_KIND)) || true

    fun parseRepoFields(level: Int)          = parseFieldFrom(level      , SOURCE_REPO_FIELDS)
    fun parseBuildInformation(level: Int)    = parseFieldFrom(level      , BUILD_INFO_FIELDS )

    fun parseExactSection(level: Int, key: String, parseAfterInfo: CabalParser.(Int) -> Boolean, parseBody: (Int) -> Boolean)
                                                                                                     = start(SECTION_TYPES.get(key)!!) {
        if (parseSectionType(key)) {
            (parseAfterInfo(level) && isLastOnThisLine()) || parseInvalidLine()
            parseProperties(level, { parseBody(it) }, canContainIf = (key in BUILD_INFO_SECTIONS))
        }
        else false
    }

    fun parseTopSection(level: Int, key: String) = parseExactSection(level, key, SECTIONS.get(key)!!.first) {
        parseFieldFrom(it, SECTIONS.get(key)!!.second!!)
    }

    fun parseIfOrElse(level: Int, key: String, parseFields: CabalParser.(Int) -> Boolean) = parseExactSection(level, key, SECTIONS.get(key)!!.first) {
        parseFields(it)
    }

    fun parseIfElse(level: Int, parseFields: CabalParser.(Int) -> Boolean): Boolean {
        if (parseIfOrElse(level, "if", parseFields)) {
            if (nextLevel() == level) {
                val marker = builder.mark()!!
                skipNewLineBiggerLevel(level - 1)
                if (parseIfOrElse(level, "else", parseFields)) {
                    marker.drop()
                }
                else {
                    marker.rollbackTo()
                }
            }
            return true
        }
        return false
    }

    fun parseTopSection(level: Int): Boolean {
        for (key in TOP_SECTION_NAMES) {
            if (parseTopSection(level, key)) return true
        }
        return false
    }

    fun parseTopLevelField(firstIndent: Int) = parseFieldFrom(firstIndent, PKG_DESCR_FIELDS)

    fun parseInternal(root: IElementType): ASTNode {

        fun parseSomeField(level: Int) = parseTopLevelField(level)
                                      || parseTopSection(level)
                                      || parseInvalidField(level)
                                      || parseInvalidLine()

        val rootMarker = mark()
        val firstIndent = builder.getCurrentOffset()
        while (!builder.eof()) {
            val nextIndent = nextLevel()
            if ((nextIndent == null) || (nextIndent == firstIndent)) {
                skipNewLineBiggerLevel(firstIndent - 1)
                parseSomeField(firstIndent)
            }
            else {
                skipNewLineBiggerLevel(- 1)
                parseInvalidLine()
            }

        }
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }
}