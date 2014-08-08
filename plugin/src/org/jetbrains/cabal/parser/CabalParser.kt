package org.jetbrains.haskell.cabal

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import com.intellij.lang.PsiBuilder
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

    fun nextLevel() : Int? {
        var res: Int? = null
        while ((!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT)) {
            res = indentSize(builder.getTokenText()!!)
            val mark = builder.mark()!!
            builder.advanceLexer()
            if ((builder.eof()) || (builder.getTokenType() != TokenType.NEW_LINE_INDENT)) {
                mark.rollbackTo()
                break
            }
            else {
                mark.drop()
            }
        }
        return res
    }

    fun parseAllBiggerLevel(prevLevel: Int): Boolean {
        while (!builder.eof()) {
            val nextIndent = nextLevel()
            if ((nextIndent != null) && (nextIndent <= prevLevel)) {
                break
            }
            builder.advanceLexer();
        }
        return true
    }

    fun parseFreeLine(elemType: IElementType) = start(elemType) {
        while (!builder.eof() && (builder.getTokenType() != TokenType.NEW_LINE_INDENT)) {
            builder.advanceLexer()
        }
        true
    }

    fun parseFreeForm(prevLevel: Int) = start(CabalTokelTypes.FREE_FORM, { parseAllBiggerLevel(prevLevel) })

    fun parseInvalidLine() = parseFreeLine(CabalTokelTypes.INVALID_VALUE)

    fun parseInvalidValue(prevLevel: Int) = start(CabalTokelTypes.INVALID_VALUE, { parseAllBiggerLevel(prevLevel) })

    fun parseIDValue(elemType: IElementType) = start(elemType, { token(CabalTokelTypes.ID) })

    fun parseTokenValue(elemType: IElementType) = parseFreeLine(elemType)

    fun parsePath() = parseTokenValue(CabalTokelTypes.PATH)

    fun skipNewLines(level: Int = -1) {
        val nextIndent = nextLevel()
        if ((nextIndent != null) && (nextIndent > level))
            builder.advanceLexer()
    }

    fun isLastOnThisLevel(prevLevel: Int) : Boolean {
        val nextIndent = nextLevel()
        if (builder.eof() || ((nextIndent != null) && (nextIndent <= prevLevel))) {
            return true
        }
        skipNewLines()
        return false
    }

    fun isLastOnThisLine() : Boolean = (builder.eof() || (builder.getTokenType() == TokenType.NEW_LINE_INDENT))

    fun parseValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, onOneLine: Boolean, tillEnd: Boolean = true) : Boolean {
        var res = parseValue()
        while ((!builder.eof()) && res) {

            val mark = builder.mark()!!
            if (!onOneLine) {
                if (isLastOnThisLevel(prevLevel)) {
                    mark.rollbackTo()
                    break
                }
            }

            res = parseSeparator()
            if (!tillEnd && !res) {
                res = true
                mark.rollbackTo()
                break
            }
            mark.drop()

            res = res && (onOneLine || !isLastOnThisLevel(prevLevel))
            res = res && parseValue()
        }
        return res
    }

    fun parseTokenList(level: Int) = parseValueList(level, { parseTokenValue(CabalTokelTypes.TOKEN) }, { token(CabalTokelTypes.COMMA) || true }, false)

    fun parseIDList(level: Int) = parseValueList(level, { parseIDValue(CabalTokelTypes.IDENTIFIER) }, { token(CabalTokelTypes.COMMA) || true }, false)

    fun parseOptionList(level: Int) = parseValueList(level, { parseIDValue(CabalTokelTypes.OPTION) }, { token(CabalTokelTypes.COMMA) || true }, false)

    fun parsePathList(prevLevel: Int) = parseValueList(prevLevel, { parsePath() }, { token(CabalTokelTypes.COMMA) || true }, false)

    fun parseComplexVersionConstraint(prevLevel : Int, onOneLine: Boolean = false) = start(CabalTokelTypes.COMPLEX_CONSTRAINT) {
        parseValueList(prevLevel, { parseSimpleVersionConstraint() }, { token(CabalTokelTypes.LOGIC) }, onOneLine, false)
    }

    fun parseFullVersionConstraint(prevLevel: Int, tokenType: IElementType, onOneLine: Boolean = false) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        parseIDValue(tokenType)
                && (parseComplexVersionConstraint(prevLevel, onOneLine) || true)
    }

    fun parseSimpleCondition(level: Int) = start(CabalTokelTypes.SIMPLE_CONDITION) {
        var res = parseBool()
        if (!res) {
            val testName = builder.getTokenText()
            res = token(CabalTokelTypes.ID) && token(CabalTokelTypes.OPEN_PAREN)
            if (res) {
                when (testName) {
                    "impl" -> res = parseFullVersionConstraint(level, CabalTokelTypes.COMPILER, true)
                    "flag" -> res = parseIDValue(CabalTokelTypes.NAME)
                    else   -> res = parseIDValue(CabalTokelTypes.IDENTIFIER)
                }
                res = res && token(CabalTokelTypes.CLOSE_PAREN)
            }
        }
        res
    }

    fun parseInvalidConditionPart() = start(CabalTokelTypes.INVALID_CONDITION_PART) {
        while (!builder.eof() && ((builder.getTokenType() == CabalTokelTypes.ID)
                                        || (builder.getTokenType() == CabalTokelTypes.OPEN_PAREN)
                                        || (builder.getTokenType() == CabalTokelTypes.COMPARATOR))) {
            if ((builder.getTokenText() == "flag") && token(CabalTokelTypes.ID) && token(CabalTokelTypes.OPEN_PAREN)) {
                parseIDValue(CabalTokelTypes.NAME)
            }
            else builder.advanceLexer()
        }
        true
    }

    fun parseConditionPart(level: Int): Boolean {
        if (token(CabalTokelTypes.NEGATION)) return parseConditionPart(level)
        if (token(CabalTokelTypes.OPEN_PAREN)) {
            return (parseCondition(level) && (token(CabalTokelTypes.CLOSE_PAREN) || true))
        }
        return parseSimpleCondition(level) || parseInvalidConditionPart()
    }

    fun parseCondition(level: Int) = parseValueList(level, { parseConditionPart(level) }, { token(CabalTokelTypes.LOGIC) }, true, false)

    fun parseCompilerList(level: Int) = parseConstraintList(level, CabalTokelTypes.COMPILER)

    fun parseConstraintList(prevLevel: Int, tokenType: IElementType = CabalTokelTypes.IDENTIFIER)
            = parseValueList(prevLevel, { parseFullVersionConstraint(prevLevel, tokenType) }, { token(CabalTokelTypes.COMMA) }, false)

    fun parseField(level: Int, key : String?, parseValue : (Int) -> Boolean) = start(PROPERTY_FIELD_TYPES.get(key)!!) {
        var res = parsePropertyKey(key) && token(CabalTokelTypes.COLON)
        if (res) {
            skipNewLines(level)
            (parseValue(level) && isLastOnThisLevel(level)) || parseInvalidValue(level)
        }
        else false
    }

    fun parseFieldVariety(level: Int, names: List<String>, parseValue : (Int) -> Boolean) : Boolean {
        var res = false
        for (name in names) {
            res = res || parseField(level, name, parseValue)
            if (res) break
        }
        return res
    }

    fun parseInvalidProperty(level: Int) = start(CabalTokelTypes.INVALID_PROPERTY) {
        var res = parsePropertyKey(null) && token(CabalTokelTypes.COLON)
        if (res) {
            skipNewLines(level)
            parseInvalidValue(level)
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



    fun parseProperties(prevLevel: Int, canContainIf: Boolean, parseFields: (Int) -> Boolean): Boolean {
        var currentLevel : Int? = null
        while (!builder.eof()) {
            val level = nextLevel()
            if (level == null) return false
            if ((currentLevel == null) && (level <= prevLevel)) return true                  //sections without any field is allowed
            else if ((currentLevel == null) && (level > prevLevel)) {
                currentLevel = level
            } else if (level != currentLevel!!) return (level <= prevLevel)

            skipNewLines()
            var res = parseFields(currentLevel!!) || (canContainIf && parseIfElse(currentLevel!!, parseFields)) || parseInvalidProperty(currentLevel!!)

            if (!res) {
                builder.advanceLexer()
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
            var res = parseAfterInfo() && isLastOnThisLine()
            if (!res) {
                parseInvalidLine()
            }
            parseProperties(level, (name == "library") || (name == "executable"), { parseBody(it) })
        }
        else false
    }

    fun parseIfElse(level: Int, parseFields: (Int) -> Boolean): Boolean {
        if (parseExactSection(level, "if", { start(CabalTokelTypes.FULL_CONDITION, { parseCondition(level) }) }, parseFields)) {
            if (nextLevel() == level) {
                val marker = builder.mark()!!
                skipNewLines()
                val isElse = parseExactSection(level, "else", { true }, parseFields)
                if (isElse) {
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
            if (!(parseTopLevelField() || parseSection(0) || parseInvalidProperty(0)))
                builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }
}