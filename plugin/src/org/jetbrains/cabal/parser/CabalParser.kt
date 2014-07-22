package org.jetbrains.haskell.cabal

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.haskell.parser.rules.BaseParser
import com.siyeh.ig.dataflow.BooleanVariableAlwaysNegatedInspectionBase
import org.jetbrains.haskell.parser.HaskellToken


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

    fun parseCondition(level: Int) = start(CabalTokelTypes.CONDITION) {
        parseBool()
                || (token(CabalTokelTypes.ID) && token(CabalTokelTypes.OPEN_PAREN)
                                              && ((parseFullVersionConstraint(level)) || (token(CabalTokelTypes.ID)))
                                              && token(CabalTokelTypes.CLOSE_PAREN))
    }

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

    fun parseFreeForm(prevLevel: Int) = start(CabalTokelTypes.FREE_FORM) {
        while (!builder.eof()) {
            val nextIndent = nextLevel()
            if ((nextIndent != null) && (nextIndent <= prevLevel)) {
                break
            }
            builder.advanceLexer();
        }
        true
    }

    fun parseFreeLine(): Boolean {
        while (!builder.eof() && (builder.getTokenType() != TokenType.NEW_LINE_INDENT)) {
            builder.advanceLexer()
        }
        return true
    }

    fun parseIDValue(elemType: IElementType) = start(elemType, { token(CabalTokelTypes.ID) })

    fun parseTokenValue(elemType: IElementType) = start(elemType, { parseFreeLine() })

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

    fun parseValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean, tillLessIndent: Boolean = true) : Boolean {
        var res = parseValue()
        while ((!builder.eof()) && res) {
            if (isLastOnThisLevel(prevLevel)) break

            res = parseSeparator()
            if (!tillLessIndent && !res) break
            res = res && (!isLastOnThisLevel(prevLevel))
            res = res && parseValue()
        }
        return res
    }

    fun parseTokenList(level: Int) = parseValueList(level, { parseTokenValue(CabalTokelTypes.TOKEN) }, { token(CabalTokelTypes.COMMA) || true })

    fun parseIDList(level: Int) = parseValueList(level, { parseIDValue(CabalTokelTypes.IDENTIFIER) }, { token(CabalTokelTypes.COMMA) || true })

    fun parseOptionList(level: Int) = parseValueList(level, { parseIDValue(CabalTokelTypes.OPTION) }, { token(CabalTokelTypes.COMMA) || true })

    fun parseDirectoryList(prevLevel: Int) = parseValueList(prevLevel, { parseTokenValue(CabalTokelTypes.DIRECTORY) }, { token(CabalTokelTypes.COMMA) || true })

    fun parseComplexVersionConstraint(prevLevel : Int) = parseValueList(prevLevel, { parseSimpleVersionConstraint() }, { token(CabalTokelTypes.LOGIC) })

    fun parseFullVersionConstraint(prevLevel: Int, tokenType: IElementType = CabalTokelTypes.IDENTIFIER) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        var res = parseIDValue(tokenType)
        parseComplexVersionConstraint(prevLevel)
        res
    }

    fun parseCompilerList(level: Int) = parseConstraintList(level, CabalTokelTypes.COMPILER)

    fun parseConstraintList(prevLevel: Int, tokenType: IElementType = CabalTokelTypes.IDENTIFIER)
            = parseValueList(prevLevel, { parseFullVersionConstraint(prevLevel, tokenType) }, { token(CabalTokelTypes.COMMA) })

    fun parseField(level: Int, tokenType : IElementType, key : String?, parseValue : (Int) -> Boolean) = start(tokenType) {
        var res = parsePropertyKey(key) && token(CabalTokelTypes.COLON)
        skipNewLines(level)
        res && parseValue(level) && isLastOnThisLevel(level)
    }

    fun parseFieldVariety(level: Int, tokenType : IElementType, names: List<String>, parseValue : (Int) -> Boolean) : Boolean {
        var res = false
        for (name in names) {
            res = res || parseField(level, tokenType, name, parseValue)
            if (res) break
        }
        return res
    }

    fun parseTopLevelField() =
               parseField(0, CabalTokelTypes.VERSION         , "version"                , { start(CabalTokelTypes.VERSION_VALUE, { parseVersion() }) })
            || parseField(0, CabalTokelTypes.CABAL_VERSION   , "cabal-version"          , { parseComplexVersionConstraint(0) })
            || parseField(0, CabalTokelTypes.NAME_FIELD      , "name"                   , { parseIDValue(CabalTokelTypes.NAME) })
            || parseField(0, CabalTokelTypes.BUILD_TYPE_FIELD, "build-type"             , { parseIDValue(CabalTokelTypes.BUILD_TYPE) })
            || parseField(0, CabalTokelTypes.LICENSE         , "license"                , { parseIDValue(CabalTokelTypes.IDENTIFIER) })
            || parseField(0, CabalTokelTypes.TESTED_WITH     , "tested-with"            , { parseCompilerList(it) })
            || parseField(0, CabalTokelTypes.LICENSE_FILES   , "license-file"           , { parseIDValue(CabalTokelTypes.DIRECTORY) })
            || parseField(0, CabalTokelTypes.LICENSE_FILES   , "license-files"          , { parseDirectoryList(it) })
            || parseField(0, CabalTokelTypes.DIRECTORY_FIELD , "data-dir"               , { parseTokenValue(CabalTokelTypes.DIRECTORY) })
            || parseField(0, CabalTokelTypes.MAINTAINER      , "maintainer"             , { parseTokenValue(CabalTokelTypes.E_MAIL) })
            || parseFieldVariety(0, CabalTokelTypes.URL_FIELD   , URL_FIELD_NAMES          , { parseTokenValue(CabalTokelTypes.URL) })
            || parseFieldVariety(0, CabalTokelTypes.FILE_LIST   , TOP_FILE_LIST_FIELD_NAMES, { parseDirectoryList(it) })
            || parseFieldVariety(0, CabalTokelTypes.FREE_FIELD  , FREE_FORM_FIELD_NAMES    , { parseFreeForm(it) })

    fun parseMainFile(level: Int) = parseField(level, CabalTokelTypes.MAIN_FILE, "main-is", { parseIDValue(CabalTokelTypes.DIRECTORY) })

    fun parseRepoFields(level: Int) =
               parseField(level, CabalTokelTypes.URL_FIELD         , "location"         , { parseTokenValue(CabalTokelTypes.URL) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "type"             , { parseIDValue(CabalTokelTypes.REPO_TYPE) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "module"           , { parseTokenValue(CabalTokelTypes.URL) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "branch"           , { parseTokenValue(CabalTokelTypes.URL) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "tag"              , { parseTokenValue(CabalTokelTypes.URL) })
            || parseField(level, CabalTokelTypes.DIRECTORY_FIELD   , "subdir"           , { parseTokenValue(CabalTokelTypes.DIRECTORY) })

    fun parseBuildInformation(level: Int) =
               parseField(level, CabalTokelTypes.BUILD_DEPENDS     , "build-depends"    , { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.PKG_CONFIG_DEPENDS, "pkgconfig-depends", { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.BUILD_TOOLS       , "build-tools"      , { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.BUILDABLE         , "buildable"        , { parseBool() })
            || parseField(level, CabalTokelTypes.EXTENSIONS        , "extensions"       , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.OTHER_MODULES     , "other-modules"    , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.FILE_LIST         , "includes"         , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.FILE_LIST         , "install-includes" , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.FILE_LIST         , "c-sources"        , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.DIRECTORY_LIST    , "hs-source-dirs"   , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.DIRECTORY_LIST    , "extra-lib-dirs"   , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.DIRECTORY_LIST    , "include-dirs"     , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.TOKEN_LIST        , "frameworks"       , { parseTokenList(it) })
            || parseField(level, CabalTokelTypes.TOKEN_LIST        , "extra-libraries"  , { parseTokenList(it) })
            || parseFieldVariety(level, CabalTokelTypes.OPTIONS_FIELD , OPTIONS_FIELD_NAMES, { parseOptionList(it) })



    fun parseIf(level: Int, parseSectionFields: (Int) -> Boolean) = start(CabalTokelTypes.IF_CONDITION) {
        start(CabalTokelTypes.SECTION_TYPE) { matchesIgnoreCase(CabalTokelTypes.ID, "if") } && parseCondition(level)
                && parseProperties(level, true, parseSectionFields)
    }

    fun parseElse(level: Int, parseSectionFields: (Int) -> Boolean) = start(CabalTokelTypes.ELSE_CONDITION) {
        skipNewLines(level - 1)
        start(CabalTokelTypes.SECTION_TYPE) { matchesIgnoreCase(CabalTokelTypes.ID, "else") }
                && parseProperties(level, true, parseSectionFields);
    }

    fun parseProperties(prevLevel: Int, canContainIf: Boolean, parseSectionFields: (Int) -> Boolean): Boolean {
        var currentLevel : Int? = null
        while (!builder.eof()) {
            val level = nextLevel()
            if ((level == null) || ((currentLevel == null) && (level <= prevLevel))) return false            //sections without any field is not allowed
            else if ((currentLevel == null) && (level > prevLevel)) {
                currentLevel = level
            } else if (level != currentLevel!!) return (level <= prevLevel)

            skipNewLines()
            var res = parseSectionFields(currentLevel!!)
                    || parseBuildInformation(currentLevel!!)
                    || (canContainIf && parseIfElse(currentLevel!!, parseSectionFields))

            if (!res) {
                builder.advanceLexer()
            }
        }
        return true
    }

    fun parseIfElse(level: Int, parseSectionFields: (Int) -> Boolean): Boolean {
        if (parseIf(level, parseSectionFields)) {
            if (nextLevel() == level) {
                parseElse(level, parseSectionFields)
            }
            return true
        }
        return false
    }

    fun parseSectionType(name: String) = start(CabalTokelTypes.SECTION_TYPE) {
        matchesIgnoreCase(CabalTokelTypes.ID, name)
    }

    fun parseExecutable(level: Int) = start(CabalTokelTypes.EXECUTABLE) {
        parseSectionType("executable")
                && parseIDValue(CabalTokelTypes.NAME)
                && parseProperties(level, true, { parseMainFile(it) })
    }

    fun parseExactSection(level: Int, tokenType: IElementType, sectionName: String, parseAfterInfo: () -> Boolean, parseBody: (Int) -> Boolean) = start(tokenType) {
        parseSectionType(sectionName)
                && parseAfterInfo()
                && parseProperties(level, (sectionName == "library"), { parseBody(it) })
    }

    fun parseSection(level: Int) =
               parseExecutable(level)

            || parseExactSection(level, CabalTokelTypes.TEST_SUITE, "test-suite", { parseIDValue(CabalTokelTypes.NAME) }) {
                              parseMainFile(it)
                           || parseField(it, CabalTokelTypes.TOKEN_FIELD , "type"       , { parseIDValue(CabalTokelTypes.TEST_SUIT_TYPE) })
                           || parseField(it, CabalTokelTypes.TOKEN_FIELD , "test-module", { parseIDValue(CabalTokelTypes.IDENTIFIER) })
               }

            || parseExactSection(level, CabalTokelTypes.LIBRARY, "library", { true }) {
                              parseField(it, CabalTokelTypes.EXPOSED_MODULES, "exposed-modules", { parseIDList(it) })
                           || parseField(it, CabalTokelTypes.EXPOSED        , "exposed"        , { parseBool() })
               }

            || parseExactSection(level, CabalTokelTypes.BENCHMARK, "benchmark", { parseIDValue(CabalTokelTypes.NAME) }) {
                              parseMainFile(it)
                           || parseField(it, CabalTokelTypes.TOKEN_FIELD , "type"       , { parseIDValue(CabalTokelTypes.BENCHMARK_TYPE) })
               }

            || parseExactSection(level, CabalTokelTypes.SOURCE_REPO, "source-repository", { parseIDValue(CabalTokelTypes.REPO_KIND) }) {
                              parseRepoFields(it)
               }

            || parseExactSection(level, CabalTokelTypes.FLAG, "flag", { parseIDValue(CabalTokelTypes.NAME) }) {
                              parseField(it, CabalTokelTypes.FREE_FIELD  , "description", { parseFreeForm(it) })
                           || parseField(it, CabalTokelTypes.BOOL_FIELD  , "default"    , { parseBool()       })
                           || parseField(it, CabalTokelTypes.BOOL_FIELD  , "manual"     , { parseBool()       })
               }

    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()
        while (!builder.eof()) {
            if (!(parseTopLevelField() || parseSection(0)))
              builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }
}