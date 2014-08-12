package org.jetbrains.haskell.cabal

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.haskell.parser.rules.BaseParser
import org.jetbrains.haskell.parser.HaskellToken
import com.siyeh.ig.dataflow.BooleanVariableAlwaysNegatedInspectionBase


class CabalParser(root: IElementType, builder: PsiBuilder) : BaseParser(root, builder) {

    class object {
        public val OPTIONS_FIELD_NAMES : List<String> = listOf (
                "ghc-options",
                "ghc-prof-options",
                "ghc-shared-options",
                "hugs-options",
                "nhc98-options",
                "cc-options",
                "cpp-options",
                "ld-options"
        )

        public val FREE_FORM_FIELD_NAMES : List<String> = listOf (
                "copyright",
                "author",
                "stability",
                "synopsis",
                "description",
                "category"
        )

        public val TOP_FILE_LIST_FIELD_NAMES : List<String> = listOf (
                "extra-doc-files",
                "extra-tmp-files",
                "data-files",
                "extra-source-files"
        )

        public val URL_FIELD_NAMES : List<String> = listOf (
                "package-url",
                "homepage",
                "bug-reports"
        )
    }

    public fun parse(): ASTNode = parseInternal(root)

    fun parsePropertyKey(propName : String?) = start(CabalTokelTypes.PROPERTY_KEY) {
        if (propName == null) token(CabalTokelTypes.ID) else matchesIgnoreCase(CabalTokelTypes.ID, propName)
    }

    fun parseBool() = matchesIgnoreCase(CabalTokelTypes.ID, "true") || matchesIgnoreCase(CabalTokelTypes.ID, "false")

    fun parseVersion() = token(CabalTokelTypes.NUMBER) || token(CabalTokelTypes.ID)

    fun parseSimpleVersionConstraint() = start(CabalTokelTypes.VERSION_CONSTRAINT) {
        token(CabalTokelTypes.COMPARATOR) && parseVersion()
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

    fun canParse(parse: () -> Boolean): Boolean {
        val marker = builder.mark()!!
        val res = parse()
        marker.rollbackTo()
        return res
    }

    fun isLastOnThisLine() : Boolean = (builder.eof() || (builder.getTokenType() == TokenType.NEW_LINE_INDENT))

    fun skipNewLine(level: Int = -1) {
        val nextIndent = nextLevel()
        if ((nextIndent != null) && (nextIndent > level))
            builder.advanceLexer()
    }

    fun isLastOnThisLevel(prevLevel: Int) : Boolean {
        val nextIndent = nextLevel()
        if (builder.eof() || ((nextIndent != null) && (nextIndent <= prevLevel))) {
            return true
        }
        return false
    }

    fun skipAllBiggerLevelTill(prevLevel: Int, parseSeparator: () -> Boolean) {
        while (!builder.eof()) {
            if (isLastOnThisLevel(prevLevel)) {
                break
            }
            if (canParse({ skipNewLine(prevLevel); parseSeparator() })) {
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

    fun parseFreeLine(elemType: IElementType) = start(elemType) {
        while (!isLastOnThisLine()) {
            builder.advanceLexer()
        }
        true
    }

    fun parseInvalidLine() = parseFreeLine(CabalTokelTypes.INVALID_VALUE)

    fun parseAsInvalid(parseBody: () -> Boolean) = start(CabalTokelTypes.INVALID_VALUE, { parseBody() })

    fun parseFreeForm(prevLevel: Int) = start(CabalTokelTypes.FREE_FORM, { skipAllBiggerLevelTill(prevLevel, parseSeparator = { false }); true })

    fun parseInvalidValueTillSeparator(prevLevel: Int, parseSeparator: () -> Boolean, onOneLine: Boolean) = start(CabalTokelTypes.INVALID_VALUE) {
        if (!onOneLine) {
            skipAllBiggerLevelTill(prevLevel, parseSeparator)
        }
        else {
            skipFreeLineTill(parseSeparator)
        }
        true
    }

    fun parseIDValue(elemType: IElementType) = start(elemType, { token(CabalTokelTypes.ID) })

    fun parseTokenValue(elemType: IElementType) = parseFreeLine(elemType)                               // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    fun parsePath() = parseTokenValue(CabalTokelTypes.PATH)                                             // !!!!!

    fun parseTillSeparatorOrPrevLevel(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean, separatorIsOptional: Boolean) : Boolean {
        if (!onOneLine) skipNewLine(prevLevel)                                                          // returns false if there is nothing to parse
        val mark = builder.mark()!!
        var valueParsed = parseValue()
        if (!onOneLine) skipNewLine(prevLevel)
        if (valueParsed && (isLastOnThisLevel(prevLevel) || canParse({ parseSeparator() }) || separatorIsOptional)) {
            mark.drop()
        }
        else {
            mark.rollbackTo()
            parseInvalidValueTillSeparator(prevLevel, parseSeparator, onOneLine)
            if (!onOneLine) skipNewLine(prevLevel)
        }
        return true
    }

    fun parseTillEndValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean, separatorIsOptional: Boolean) : Boolean {
        do {
            parseTillSeparatorOrPrevLevel(prevLevel, parseValue, parseSeparator, onOneLine, separatorIsOptional)
        } while ((!builder.eof()) && !isLastOnThisLevel(prevLevel) && (parseSeparator() || separatorIsOptional))
        return true
    }

    fun parseTillValidValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean) : Boolean {
        var mark: Marker? = builder.mark()!!
        var nonEmpty = false
        do {
            if (!onOneLine) skipNewLine(prevLevel);
            if (parseValue()) {
                mark?.drop()
                nonEmpty = true
            }
            else break
            mark = builder.mark()!!
            if (!onOneLine) skipNewLine(prevLevel);
        } while ((!builder.eof()) && parseSeparator())
        mark?.rollbackTo()
        return nonEmpty
    }

    fun parseCommonCommaList(level: Int, parseBody: () -> Boolean) = parseTillEndValueList(
            level,
            parseBody,
            { token(CabalTokelTypes.COMMA) },
            onOneLine = false,
            separatorIsOptional = true
    )

    fun parseTokenList(level: Int)  = parseCommonCommaList(level, { parseTokenValue(CabalTokelTypes.TOKEN) })

    fun parseIDList(level: Int)     = parseCommonCommaList(level, { parseIDValue(CabalTokelTypes.IDENTIFIER) })

    fun parseOptionList(level: Int) = parseCommonCommaList(level, { parseIDValue(CabalTokelTypes.OPTION) })

    fun parsePathList(level: Int)   = parseCommonCommaList(level, { parsePath() })

    fun parseComplexVersionConstraint(prevLevel : Int, onOneLine: Boolean = false) = start(CabalTokelTypes.COMPLEX_CONSTRAINT) {
        parseTillValidValueList(prevLevel, { parseSimpleVersionConstraint() }, { token(CabalTokelTypes.LOGIC) }, onOneLine)
    }

    fun parseFullVersionConstraint(prevLevel: Int, tokenType: IElementType, onOneLine: Boolean = false) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        parseIDValue(tokenType)
                && (parseComplexVersionConstraint(prevLevel, onOneLine) || true)
    }

    fun parseSimpleCondition(level: Int) = start(CabalTokelTypes.SIMPLE_CONDITION) {
        val testName = builder.getTokenText()
        if (parseBool()) {
            true
        }
        else if (token(CabalTokelTypes.ID) && token(CabalTokelTypes.OPEN_PAREN)) {
            var res: Boolean
            when (testName) {
                "impl" -> res = parseFullVersionConstraint(level, CabalTokelTypes.COMPILER, true)
                "flag" -> res = parseIDValue(CabalTokelTypes.NAME)
                else   -> res = parseIDValue(CabalTokelTypes.IDENTIFIER)
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
                parseIDValue(CabalTokelTypes.NAME)
            }
            else builder.advanceLexer()
        }
        true
    }

    fun parseConditionPart(level: Int): Boolean = start(CabalTokelTypes.CONDITION_PART) {
        if (token(CabalTokelTypes.NEGATION)) {
            parseConditionPart(level)
        }
        else if (token(CabalTokelTypes.OPEN_PAREN)) {
            parseCondition(level) && (token(CabalTokelTypes.CLOSE_PAREN) || true)
        }
        else {
            parseSimpleCondition(level)
        }
    }

    fun parseCondition(level: Int) = parseTillValidValueList(
            level,
            { parseConditionPart(level) || parseInvalidConditionPart() },
            { token(CabalTokelTypes.LOGIC) || (parseInvalidConditionPart() && token(CabalTokelTypes.LOGIC)) },
            onOneLine = true
    )

    fun parseConstraintList(prevLevel: Int, tokenType: IElementType = CabalTokelTypes.IDENTIFIER) = parseTillEndValueList(
            prevLevel,
            { parseFullVersionConstraint(prevLevel, tokenType) },
            { token(CabalTokelTypes.COMMA) },
            onOneLine = false,
            separatorIsOptional = false
    )

    fun parseCompilerList(level: Int) = parseConstraintList(level, CabalTokelTypes.COMPILER)

    fun parseField(level: Int, key : String?, parseValue : (Int) -> Boolean) = start(PROPERTY_FIELD_TYPES.get(key)!!) {
        if (parsePropertyKey(key) && token(CabalTokelTypes.COLON)) {
            skipNewLine(level)
            (parseValue(level) && isLastOnThisLevel(level)) || parseInvalidValueTillSeparator(level, parseSeparator = { false }, onOneLine = false)
        }
        else false
    }

    fun parseFieldVariety(level: Int, names: List<String>, parseValue : (Int) -> Boolean) : Boolean {
        for (name in names) {
            if (parseField(level, name, parseValue)) return true
        }
        return false
    }

    fun parseInvalidField(level: Int) = start(CabalTokelTypes.INVALID_FIELD) {
        if (parseIDValue(CabalTokelTypes.NAME)) {
            token(CabalTokelTypes.COLON)
            skipAllBiggerLevelTill(level, parseSeparator = { false })
            true
        }
        else false
    }

    fun parseTopLevelField() =
               parseField(0, "version"                , { start(CabalTokelTypes.VERSION_VALUE, { parseVersion() }) })
            || parseField(0, "cabal-version"          , { parseSimpleVersionConstraint() })
            || parseField(0, "name"                   , { parseIDValue(CabalTokelTypes.NAME) })
            || parseField(0, "build-type"             , { parseIDValue(CabalTokelTypes.BUILD_TYPE) })
            || parseField(0, "license"                , { parseIDValue(CabalTokelTypes.IDENTIFIER) })
            || parseField(0, "tested-with"            , { parseCompilerList(it) })
            || parseField(0, "license-file"           , { parsePath() })
            || parseField(0, "license-files"          , { parsePathList(it) })
            || parseField(0, "data-dir"               , { parsePath() })
            || parseField(0, "maintainer"             , { parseTokenValue(CabalTokelTypes.E_MAIL) })
            || parseFieldVariety(0, URL_FIELD_NAMES          , { parseTokenValue(CabalTokelTypes.URL) })
            || parseFieldVariety(0, TOP_FILE_LIST_FIELD_NAMES, { parsePathList(it) })
            || parseFieldVariety(0, FREE_FORM_FIELD_NAMES    , { parseFreeForm(it) })

    fun parseMainFile(level: Int) = parseField(level, "main-is", { parsePath() })

    fun parseRepoFields(level: Int) =
               parseField(level, "location"         , { parseTokenValue(CabalTokelTypes.URL) })
            || parseField(level, "type"             , { parseIDValue(CabalTokelTypes.REPO_TYPE) })
            || parseField(level, "module"           , { parseTokenValue(CabalTokelTypes.TOKEN) })
            || parseField(level, "branch"           , { parseTokenValue(CabalTokelTypes.TOKEN) })
            || parseField(level, "tag"              , { parseTokenValue(CabalTokelTypes.TOKEN) })
            || parseField(level, "subdir"           , { parsePath() })

    fun parseBuildInformation(level: Int) =
               parseField(level, "build-depends"    , { parseConstraintList(it) })
            || parseField(level, "pkgconfig-depends", { parseConstraintList(it) })
            || parseField(level, "build-tools"      , { parseConstraintList(it) })
            || parseField(level, "buildable"        , { parseBool() })
            || parseField(level, "extensions"       , { parseIDList(it) })
            || parseField(level, "other-modules"    , { parseIDList(it) })
            || parseField(level, "includes"         , { parsePathList(it) })
            || parseField(level, "install-includes" , { parsePathList(it) })
            || parseField(level, "c-sources"        , { parsePathList(it) })
            || parseField(level, "hs-source-dirs"   , { parsePathList(it) })
            || parseField(level, "hs-source-dir"    , { parsePath() })
            || parseField(level, "extra-lib-dirs"   , { parsePathList(it) })
            || parseField(level, "include-dirs"     , { parsePathList(it) })
            || parseField(level, "frameworks"       , { parseTokenList(it) })
            || parseField(level, "extra-libraries"  , { parseTokenList(it) })
            || parseFieldVariety(level, OPTIONS_FIELD_NAMES, { parseOptionList(it) })


    fun parseProperties(prevLevel: Int, parseFields: (Int) -> Boolean, canContainIf: Boolean): Boolean {
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
            skipNewLine()
            if ((currentLevel != null) && (level != currentLevel!!) && (level > prevLevel)) {
                parseInvalidValueTillSeparator(currentLevel!!, parseSeparator = { false }, onOneLine = false)
            }
            else {
                parseFields(level) || (canContainIf && parseIfElse(level, parseFields)) || parseInvalidField(level) || parseInvalidLine()
            }
        }
        return true
    }

    fun parseSectionType(name: String) = start(CabalTokelTypes.SECTION_TYPE) {
        matchesIgnoreCase(CabalTokelTypes.ID, name)
    }

    fun parseRepoKinds() = (parseIDValue(CabalTokelTypes.REPO_KIND) && parseIDValue(CabalTokelTypes.REPO_KIND)) || true

    fun parseExactSection(level: Int, name: String, parseAfterInfo: () -> Boolean, parseBody: (Int) -> Boolean)
                                                                                                     = start(SECTION_TYPES.get(name)!!) {
        if (parseSectionType(name)) {
            (parseAfterInfo() && isLastOnThisLine()) || parseInvalidLine()
            parseProperties(level, { parseBody(it) }, canContainIf = (name == "library") || (name == "executable"))
        }
        else false
    }

    fun parseIfElse(level: Int, parseFields: (Int) -> Boolean): Boolean {
        if (parseExactSection(level, "if", { start(CabalTokelTypes.FULL_CONDITION, { parseCondition(level) }) }, parseFields)) {
            if (nextLevel() == level) {
                val marker = builder.mark()!!
                skipNewLine()
                if (parseExactSection(level, "else", { true }, parseFields)) {
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

    fun parseSection(level: Int) =
               parseExactSection(level, "executable", { parseFreeLine(CabalTokelTypes.NAME) }) {
                              parseMainFile(it)
                           || parseBuildInformation(it)
               }

            || parseExactSection(level, "test-suite", { parseFreeLine(CabalTokelTypes.NAME) }) {
                              parseMainFile(it)
                           || parseField(it, "type"       , { parseIDValue(CabalTokelTypes.TEST_SUITE_TYPE) })
                           || parseField(it, "test-module", { parseIDValue(CabalTokelTypes.IDENTIFIER) })
                           || parseBuildInformation(it)
               }

            || parseExactSection(level, "library", { true }) {
                              parseField(it, "exposed-modules", { parseIDList(it) })
                           || parseField(it, "exposed"        , { parseBool() })
                           || parseBuildInformation(it)
               }

            || parseExactSection(level, "benchmark", { parseFreeLine(CabalTokelTypes.NAME) }) {
                              parseMainFile(it)
                           || parseField(it, "type"       , { parseIDValue(CabalTokelTypes.BENCHMARK_TYPE) })
                           || parseBuildInformation(it)
               }

            || parseExactSection(level, "source-repository", { parseRepoKinds() }) {
                              parseRepoFields(it)
               }

            || parseExactSection(level, "flag", { parseFreeLine(CabalTokelTypes.NAME) }) {
                              parseField(it, "description", { parseFreeForm(it) })
                           || parseField(it, "default"    , { parseBool()       })
                           || parseField(it, "manual"     , { parseBool()       })
               }

    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()
        while (!builder.eof()) {
            if (!(parseTopLevelField() || parseSection(0) || parseInvalidField(0)))
                builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }
}