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

        public val FILE_LIST_FIELD_NAMES : List<String> = listOf (
                "extra-doc-files",
                "extra-tmp-files",
                "data-files",
                "extra-source-files"
        )
    }

    public fun parse(): ASTNode {
        return parseInternal(root)
    }

    fun parsePropertyKey(propName : String?) = start(CabalTokelTypes.PROPERTY_KEY) {
        if (propName == null) token(CabalTokelTypes.ID) else matchesIgnoreCase(CabalTokelTypes.ID, propName)
    }

    fun indentSize(str: String): Int {
        val indexOf = str.lastIndexOf('\n')
        return str.size - indexOf - 1
    }

    fun parsePropertyValue(prevLevel: Int) = start(CabalTokelTypes.PROPERTY_VALUE) {
        while (!builder.eof()) {
            if (builder.getTokenType() == TokenType.NEW_LINE_INDENT) {
                if (indentSize(builder.getTokenText()!!) <= prevLevel) {
                    break;
                }
            }
            builder.advanceLexer()
        }
        true;
    }

    fun parseName() = start(CabalTokelTypes.NAME) {
        token(CabalTokelTypes.ID)
    }

    fun parseFileName() = start(CabalTokelTypes.FILE_NAME) {
        token(CabalTokelTypes.ID)
    }

    fun parseDirectory() = start(CabalTokelTypes.FILE_REF) {
        token(CabalTokelTypes.ID)
    }

    fun parseBool(): Boolean {
        return matches(CabalTokelTypes.ID, "true") || (matches(CabalTokelTypes.ID, "false"))
    }

    fun parseCondition(level: Int) = start(CabalTokelTypes.CONDITION) {                                                   //!!!!!!!!!
        matches(CabalTokelTypes.ID, "true") || matches(CabalTokelTypes.ID, "false")
                || (token(CabalTokelTypes.ID) && token(CabalTokelTypes.LEFT_PAREN)
                                              && ((parseFullVersionConstraint(level)) || (token(CabalTokelTypes.ID)))
                                              && token(CabalTokelTypes.RIGHT_PAREN))
    }

    fun parseSimpleVersionConstraint() = start(CabalTokelTypes.VERSION_CONSTRAINT) {
        (token(CabalTokelTypes.COMPARATOR)) && (token(CabalTokelTypes.ID))
    }

    fun parseURL() = start(CabalTokelTypes.URL) {
        token(CabalTokelTypes.ID)
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

    fun skipNewLines() {
        if ((!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT))
            builder.advanceLexer();
    }

    fun isLastOnThisLevel(prevLevel: Int) : Boolean {
        if ((nextLevel() != null) && (nextLevel()!! <= prevLevel)) {
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
        parsePropertyKey(key)
                && token(CabalTokelTypes.COLON)
                && parseValue(level)
    }

    fun parseFieldList(level: Int, tokenType : IElementType, names: List<String>, parseValue : (Int) -> Boolean) : Boolean {
        var res = false
        for (name in names) {
            res = res || parseField(level, tokenType, name, parseValue)
        }
        return res
    }

    fun parseProperty(level: Int) = parseField(level, CabalTokelTypes.PROPERTY, null, { parsePropertyValue(it) })

    fun parseTopLevelField() =
               parseField(0, CabalTokelTypes.VERSION        , "version"           , { token(CabalTokelTypes.ID) && isLastOnThisLevel(it) })
            || parseField(0, CabalTokelTypes.CABAL_VERSION  , "cabal-version"     , { parseSimpleVersionConstraint() && isLastOnThisLevel(it) })
            || parseField(0, CabalTokelTypes.NAME_FIELD     , "name"              , { parseName() && isLastOnThisLevel(it) })
            || parseField(0, CabalTokelTypes.URL_FIELD      , "package-url"       , { parseURL() && isLastOnThisLevel(it) })
            || parseField(0, CabalTokelTypes.URL_FIELD      , "homepage"          , { parseURL() && isLastOnThisLevel(it) })
            || parseField(0, CabalTokelTypes.URL_FIELD      , "bug-reports"       , { parseURL() && isLastOnThisLevel(it) })
            || parseField(0, CabalTokelTypes.DIRECTORY_FIELD, "data-dir"          , { parseDirectory() && isLastOnThisLevel(it) })
//    .        || parseField(0, CabalTokelTypes.BUILD_TYPE     , "build-type"        , { token(CabalTokelTypes.ID) })
//    .        || parseField(0, CabalTokelTypes.LICENSE        , "license"           , { token(CabalTokelTypes.ID) })
//    .        || parseField(0, CabalTokelTypes.MAINTAINER     , "maintainer"        , { parseEMail() && isLastOnThisLevel(it) })
//    .        || parseField(0, CabalTokelTypes.TESTED_WITH    , "tested-with"       , { token(CabalTokelTypes.ID) })
//            || parseField(0, CabalTokelTypes.LICENSE_FILE   , "license-file"      , { parseFileName() })
//            || parseField(0, CabalTokelTypes.LICENSE_FILE   , "license-files"     , { parseFileNameList(it) })
            || parseFieldList(0, CabalTokelTypes.FILE_LIST   , FILE_LIST_FIELD_NAMES, { parseFileNameList(it) })
            || parseFieldList(0, CabalTokelTypes.FREE_FIELD  , FREE_FORM_FIELD_NAMES, { parseFreeForm(it) })

    fun parseMainFile(level: Int) = parseField(level, CabalTokelTypes.MAIN_FILE, "main-is", { parseFileName() && isLastOnThisLevel(it) })                           //

    fun parseExposedModules(level: Int) = parseField(level, CabalTokelTypes.EXPOSED_MODULES, "exposed-modules", { parseIDList(it) })
    fun parseExposed(level: Int)        = parseField(level, CabalTokelTypes.EXPOSED        , "exposed", { token(CabalTokelTypes.ID) && isLastOnThisLevel(it) })

    fun parseRepoFields(level: Int) = false
//               parseField(level, CabalTokelTypes.REPO_TYPE         , "type"             , { token(CabalTokelTypes.ID) })
            || parseField(level, CabalTokelTypes.URL_FIELD         , "location"         , { parseURL() && isLastOnThisLevel(it) })
//            || parseField(level, CabalTokelTypes.REPO_MODULE       , "module"           , { token(CabalTokelTypes.ID) })
//            || parseField(level, CabalTokelTypes.REPO_BRANCH       , "branch"           , { token(CabalTokelTypes.ID) })
//            || parseField(level, CabalTokelTypes.REPO_TAG          , "tag"              , { token(CabalTokelTypes.ID) })
//            || parseField(level, CabalTokelTypes.REPO_SUBDIR       , "subdir"           , { parseDirectory() })

    fun parseBuildInformation(level: Int) =
               parseField(level, CabalTokelTypes.BUILD_DEPENDS     , "build-depends"    , { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.PKG_CONFIG_DEPENDS, "pkgconfig-depends", { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.BUILD_TOOLS       , "build-tools"      , { parseConstraintList(it) })
            || parseField(level, CabalTokelTypes.BUILDABLE         , "buildable"        , { token(CabalTokelTypes.ID) && isLastOnThisLevel(it) })
            || parseField(level, CabalTokelTypes.EXTENSIONS        , "extensions"       , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.OTHER_MODULES     , "other-modules"    , { parseIDList(it) })
            || parseField(level, CabalTokelTypes.HS_SOURCE_DIRS    , "hs-source-dirs"   , { parseDirectoryList(it) })
//            || parseField(level, CabalTokelTypes.INCLUDES          , "includes"         , { parseFileNameList(it) })
//            || parseField(level, CabalTokelTypes.INSTALL_INCLUDES  , "install-includes" , { parseFileNameList(it) })
//            || parseField(level, CabalTokelTypes.C_SOURCES         , "c-sources"        , { parseFileNameList(it) })
//            || parseField(level, CabalTokelTypes.FRAMEWORKS        , "frameworks"       , { parseIDList(it) })
//            || parseField(level, CabalTokelTypes.EXTRA_LIB_DIRS    , "extra-lib-dirs"   , { parseDirectoryList(it) })
//            || parseField(level, CabalTokelTypes.INCLUDE_DIRS      , "include-dirs"     , { parseDirectoryList(it) })
//            || parseField(level, CabalTokelTypes.EXTRA_LIBRARIES   , "extra-libraries"  , { parseIDList(it) })
            || parseFieldList(level, CabalTokelTypes.OPTIONS_FIELD , OPTIONS_FIELD_NAMES, { parseIDList(it) })



    fun parseIf(level: Int, parseSectionFields: (Int) -> Boolean) = start(CabalTokelTypes.IF_CONDITION) {
        start(CabalTokelTypes.SECTION_TYPE) { matchesIgnoreCase(CabalTokelTypes.ID, "if") } && parseCondition(level)
                && parseProperties(level, true, parseSectionFields)
    }

    fun parseElse(level: Int, parseSectionFields: (Int) -> Boolean) = start(CabalTokelTypes.ELSE_CONDITION) {
        skipNewLines()
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
                    || parseProperty(currentLevel!!)
                    || (canContainIf && parseIfElse(currentLevel!!, parseSectionFields))

            if (!res) {
                builder.advanceLexer()                                                                        //?!?!
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
            || parseExactSection(level, CabalTokelTypes.TEST_SUITE , "test-suite"       , true , { parseMainFile(it)
                                                                                         //       || parseType(it)
                                                                                         //       || parseTestModule(it)
                                                                                                 })
            || parseExactSection(level, CabalTokelTypes.LIBRARY    , "library"          , false, { parseExposedModules(it) || parseExposed(it) })
            || parseExactSection(level, CabalTokelTypes.BENCHMARK  , "benchmark"        , true , { parseMainFile(it)
                                                                                         //       || parseType(it)
                                                                                                 })
            || parseExactSection(level, CabalTokelTypes.SOURCE_REPO, "source-repository", true , { parseRepoFields(it) })
            || parseExactSection(level, CabalTokelTypes.FLAG       , "flag"             , true ,
                                                   { parseField(0, CabalTokelTypes.FREE_FIELD  , "description", { parseFreeForm(it) })
               //                                    || parseDefault(it)
               //                                    || parseManual(it)
                                                   })

    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()
        while (!builder.eof()) {
            parseTopLevelField() || parseProperty(0) || parseSection(0)
            builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }
}