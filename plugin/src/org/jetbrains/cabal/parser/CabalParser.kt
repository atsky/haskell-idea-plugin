package org.jetbrains.haskell.cabal

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.haskell.parser.rules.BaseParser
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
                "bug-reports",
                "maintainer"
        )

//        public val LICENSE_TYPES : List<String> = listOf (
//                "GPL",
//                "GNU",
//                "AGPL",
//                "GNU",
//                "LGPL",
//                "GNU",
//                "BSD2",
//                "BSD3",
//                "BSD4",
//                "MIT",
//                "MIT",
//                "MPL",
//                "Apache",
//                "PublicDomain",
//                "AllRightsReserved",
//                "OtherLicense"
//        )
    }

    public fun parse(): ASTNode = parseInternal(root)

//    fun parsePropertyValue(prevLevel: Int) = start(CabalTokelTypes.PROPERTY_VALUE) {
//        while (!builder.eof()) {
//            if (builder.getTokenType() == TokenType.NEW_LINE_INDENT) {
//                if (indentSize(builder.getTokenText()!!) <= prevLevel) {
//                    break;
//                }
//            }
//            builder.advanceLexer()
//        }
//        true;
//    }

    fun parsePropertyKey(propName : String?) = start(CabalTokelTypes.PROPERTY_KEY) {
        if (propName == null) token(CabalTokelTypes.ID) else matchesIgnoreCase(CabalTokelTypes.ID, propName)
    }

    fun parseTokenVariety(tokens: List<String>): Boolean {
        if ((builder.getTokenType() == CabalTokelTypes.ID) && (builder.getTokenText()!! in tokens)) {
            builder.advanceLexer()
            return true;
        }
        return false;
    }

    fun parseName()      = start(CabalTokelTypes.NAME, { token(CabalTokelTypes.ID) })

    fun parseFileName()  = start(CabalTokelTypes.FILE_NAME, { token(CabalTokelTypes.ID) })

    fun parseBool() = matches(CabalTokelTypes.ID, "true") || (matches(CabalTokelTypes.ID, "false"))

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

    fun parseFreeForm(prevLevel: Int) = start(CabalTokelTypes.FREE_FORM) {
        while (!builder.eof()) {
            if ((builder.getTokenType() == TokenType.NEW_LINE_INDENT) && (indentSize(builder.getTokenText()!!) <= prevLevel)) {
                break
            }
            builder.advanceLexer();
        }
        true
    }

    fun nextLevel() : Int? {
        return if ((!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT)) {           // there cannot be two new_line_indents next to each other
            indentSize(builder.getTokenText()!!)
        }
        else null
    }

    fun skipNewLines(level: Int = -1) {
        if ((!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT) && (indentSize(builder.getTokenText()!!) > level))
            builder.advanceLexer()
    }

    fun isLastOnThisLevel(prevLevel: Int) : Boolean {
        if ((builder.eof()) || (nextLevel() != null) && (nextLevel()!! <= prevLevel)) {
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
            res = res && (!isLastOnThisLevel(prevLevel)) && parseValue()
        }
        return res
    }

    fun parseFreeLine(): Boolean {
        while (!builder.eof() && (builder.getTokenType() != TokenType.NEW_LINE_INDENT)) {
            builder.advanceLexer()
        }
        return true
    }

    fun parseDirectory() = start(CabalTokelTypes.DIRECTORY, { parseFreeLine() })

    fun parseURL() = start(CabalTokelTypes.URL, { parseFreeLine() })

    fun parseIDList(level: Int) = parseValueList(level, { token(CabalTokelTypes.ID) }, { true })

    fun parseFileNameList(prevLevel: Int) = parseValueList(prevLevel, { parseFileName() }, { true })

    fun parseDirectoryList(prevLevel: Int) = parseValueList(prevLevel, { parseDirectory() }, { true })

    fun parseComplexVersionConstraint(prevLevel : Int) = parseValueList(prevLevel, { parseSimpleVersionConstraint() }, { token(CabalTokelTypes.LOGIC) })

    fun parseFullVersionConstraint(prevLevel: Int) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        var res = token(CabalTokelTypes.ID)
        parseComplexVersionConstraint(prevLevel)
        res
    }

    fun parseConstraintList(prevLevel: Int) = parseValueList(prevLevel, { parseFullVersionConstraint(prevLevel) }, { token(CabalTokelTypes.COMMA) })

    fun parseField(level: Int, tokenType : IElementType, key : String?, parseValue : (Int) -> Boolean) = start(tokenType) {
        var res = parsePropertyKey(key) && token(CabalTokelTypes.COLON)
        skipNewLines(level)
        res && parseValue(level) && isLastOnThisLevel(level)
    }

    fun parseFieldList(level: Int, tokenType : IElementType, names: List<String>, parseValue : (Int) -> Boolean) : Boolean {
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
            || parseField(0, CabalTokelTypes.NAME_FIELD      , "name"                   , { parseName() })
            || parseField(0, CabalTokelTypes.BUILD_TYPE      , "build-type"             , { token(CabalTokelTypes.ID) })
            || parseField(0, CabalTokelTypes.LICENSE         , "license"                , { token(CabalTokelTypes.ID) })
            || parseField(0, CabalTokelTypes.TESTED_WITH     , "tested-with"            , { parseConstraintList(it) })
            || parseField(0, CabalTokelTypes.LICENSE_FILES   , "license-file"           , { parseFileName() })
            || parseField(0, CabalTokelTypes.LICENSE_FILES   , "license-files"          , { parseFileNameList(it) })
            || parseField(0, CabalTokelTypes.DIRECTORY_FIELD , "data-dir"               , { parseDirectory() })
            || parseFieldList(0, CabalTokelTypes.URL_FIELD   , URL_FIELD_NAMES          , { parseURL() })
            || parseFieldList(0, CabalTokelTypes.FILE_LIST   , TOP_FILE_LIST_FIELD_NAMES, { parseFileNameList(it) })
            || parseFieldList(0, CabalTokelTypes.FREE_FIELD  , FREE_FORM_FIELD_NAMES    , { parseFreeForm(it) })

    fun parseMainFile(level: Int) = parseField(level, CabalTokelTypes.MAIN_FILE, "main-is", { parseFileName() })

    fun parseRepoFields(level: Int) =
               parseField(level, CabalTokelTypes.URL_FIELD         , "location"         , { parseURL() })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "type"             , { token(CabalTokelTypes.ID) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "module"           , { token(CabalTokelTypes.ID) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "branch"           , { token(CabalTokelTypes.ID) })
            || parseField(level, CabalTokelTypes.TOKEN_FIELD       , "tag"              , { token(CabalTokelTypes.ID) })
            || parseField(level, CabalTokelTypes.DIRECTORY_FIELD   , "subdir"           , { parseDirectory() })

    fun parseBuildInformation(level: Int) =
               parseField(level, CabalTokelTypes.BUILD_DEPENDS     , "build-depends"    , { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.PKG_CONFIG_DEPENDS, "pkgconfig-depends", { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.BUILD_TOOLS       , "build-tools"      , { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.BUILDABLE         , "buildable"        , { parseBool() })
            || parseField(level, CabalTokelTypes.EXTENSIONS        , "extensions"       , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.OTHER_MODULES     , "other-modules"    , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.FILE_LIST         , "includes"         , { parseFileNameList(it) })
            || parseField(level, CabalTokelTypes.FILE_LIST         , "install-includes" , { parseFileNameList(it) })
            || parseField(level, CabalTokelTypes.FILE_LIST         , "c-sources"        , { parseFileNameList(it) })
            || parseField(level, CabalTokelTypes.DIRECTORY_LIST    , "hs-source-dirs"   , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.DIRECTORY_LIST    , "extra-lib-dirs"   , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.DIRECTORY_LIST    , "include-dirs"     , { parseDirectoryList(it) })
            || parseField(level, CabalTokelTypes.TOKEN_LIST        , "frameworks"       , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.TOKEN_LIST        , "extra-libraries"  , { parseIDList(it) })
            || parseFieldList(level, CabalTokelTypes.OPTIONS_FIELD , OPTIONS_FIELD_NAMES, { parseIDList(it) })



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
                && parseName()
                && parseProperties(level, true, { parseMainFile(it) })
    }

    fun parseExactSection(level: Int, tokenType: IElementType, sectionName: String, nameNeeded: Boolean, parseBody: (Int) -> Boolean) = start(tokenType) {
        parseSectionType(sectionName)
                && (!nameNeeded || parseName())
                && parseProperties(level, (sectionName == "library"), parseBody)
    }

    fun parseSection(level: Int) =
               parseExecutable(level)

            || parseExactSection(level, CabalTokelTypes.TEST_SUITE, "test-suite", true ) {
                              parseMainFile(it)
                           || parseField(it, CabalTokelTypes.TOKEN_FIELD , "type"       , { token(CabalTokelTypes.ID) })
                           || parseField(it, CabalTokelTypes.TOKEN_FIELD , "test-module", { token(CabalTokelTypes.ID) })
               }

            || parseExactSection(level, CabalTokelTypes.LIBRARY, "library", false) {
                              parseField(it, CabalTokelTypes.EXPOSED_MODULES, "exposed-modules", { parseIDList(it) })
                           || parseField(it, CabalTokelTypes.EXPOSED        , "exposed"        , { parseBool() })
               }

            || parseExactSection(level, CabalTokelTypes.BENCHMARK, "benchmark", true ) {
                              parseMainFile(it)
                           || parseField(it, CabalTokelTypes.TOKEN_FIELD , "type"       , { token(CabalTokelTypes.ID) })
               }

            || parseExactSection(level, CabalTokelTypes.SOURCE_REPO, "source-repository", true) {
                              parseRepoFields(it)
               }

            || parseExactSection(level, CabalTokelTypes.FLAG, "flag", true ) {
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