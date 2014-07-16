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

    fun findLevel(currentLevel : Int?): Int? {
        var level: Int? = null;
        val marker = builder.mark()!!
        while (builder.getTokenType() == TokenType.NEW_LINE_INDENT) {
            level = indentSize(builder.getTokenText()!!)
            builder.advanceLexer();
        }
        if (currentLevel != null && level != currentLevel) {
            marker.rollbackTo()
            return null;
        }
        marker.drop()
        return level
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

    fun isLastOnThisLevel(prevLevel: Int) : Boolean {
        while ((!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT)) {
            if (indentSize(builder.getTokenText()!!) <= prevLevel) {
                return true
            }
            builder.advanceLexer();
        }
        return false
    }

    fun parseValueList(prevLevel: Int, parseValue : () -> Boolean, parseSeparator : () -> Boolean) : Boolean {
        var res = parseValue()
        while ((!builder.eof()) && res) {
            if (isLastOnThisLevel(prevLevel)) break

            res = parseSeparator()
            if (res && !isLastOnThisLevel(prevLevel)) {
                res = parseValue()
            }
        }
        return res
    }

    fun parseIDList(level: Int) = parseValueList(level, { token(CabalTokelTypes.ID) }, { true })

    fun parseFileNameList(prevLevel: Int = 0) = parseValueList(prevLevel, { parseFileName() }, { true })

    fun parseDirectoryList(prevLevel: Int = 0) = parseValueList(prevLevel, { parseDirectory() }, { true })

    fun parseConstraintList(prevLevel: Int) = parseValueList(prevLevel, { parseFullVersionConstraint(prevLevel) }, { token(CabalTokelTypes.COMMA) })

    fun parseComplexVersionConstraint(prevLevel : Int) = parseValueList(prevLevel, { parseSimpleVersionConstraint() }, { token(CabalTokelTypes.LOGIC) })

    fun parseFullVersionConstraint(prevLevel: Int) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        var res = token(CabalTokelTypes.ID)
        parseComplexVersionConstraint(prevLevel)
        res
    }




    fun parseField(tokenType : IElementType, key : String?, parseValue : () -> Boolean) = start(tokenType) {
        parsePropertyKey(key)
                && token(CabalTokelTypes.COLON)
                && parseValue()
    }

    fun parseFieldList(tokenType : IElementType, names: List<String>, parseValue : () -> Boolean) : Boolean {
        var res = false
        for (name in names) {
            res = res || parseField(tokenType, name, parseValue)
        }
        return res
    }

    fun parseProperty(level: Int) = parseField(CabalTokelTypes.PROPERTY, null, {  parsePropertyValue(level) })

    fun parseTopLevelField() =
               parseField(CabalTokelTypes.VERSION      , "version"           , { token(CabalTokelTypes.ID) })
            || parseField(CabalTokelTypes.CABAL_VERSION, "cabal-version"     , { parseSimpleVersionConstraint() })
            || parseField(CabalTokelTypes.NAME_FIELD   , "name"              , { parseName() })
//            || parseField(CabalTokelTypes.BUILD_TYPE   , "build-type"        , { token(CabalTokelTypes.ID) })
//            || parseField(CabalTokelTypes.LICENSE      , "license"           , { token(CabalTokelTypes.ID) })
//            || parseField(CabalTokelTypes.LICENSE_FILE , "license-file"      , { parseFileName() })
//            || parseField(CabalTokelTypes.LICENSE_FILE , "license-files"     , { parseFileNameList() })
//            || parseField(CabalTokelTypes.MAINTAINER   , "copyright"         , { token(CabalTokelTypes.ID) })
            || parseField(CabalTokelTypes.PACKAGE_URL  , "package-url"       , { parseURL() })
            || parseField(CabalTokelTypes.HOMEPAGE     , "homepage"          , { parseURL() })
//            || parseField(CabalTokelTypes.BUG_REPORTS  , "bug-reports"       , { parseURL() })
//            || parseField(CabalTokelTypes.TESTED_WITH  , "tested-with"       , { token(CabalTokelTypes.ID) })
//            || parseField(CabalTokelTypes.DATA_DIR     , "data-dir"          , { token(CabalTokelTypes.ID) })
            || parseField(CabalTokelTypes.EXTRA_DOC    , "extra-doc-files"   , { parseFileNameList() })
            || parseField(CabalTokelTypes.EXTRA_TMP    , "extra-tmp-files"   , { parseFileNameList() })
            || parseField(CabalTokelTypes.DATA_FILES   , "data-files"        , { parseFileNameList() })
            || parseField(CabalTokelTypes.EXTRA_SOURCE , "extra-source-files", { parseFileNameList() })

            || parseFieldList(CabalTokelTypes.FREE_FIELD, FREE_FORM_FIELD_NAMES, { parseFreeForm(0) })


    fun parseMainFile() = parseField(CabalTokelTypes.MAIN_FILE, "main-is", { parseFileName() })

    fun parseBuildInformation(level: Int) =
               parseField(CabalTokelTypes.BUILD_DEPENDS     , "build-depends"    , { parseConstraintList(level) })
            || parseField(CabalTokelTypes.PKG_CONFIG_DEPENDS, "pkgconfig-depends", { parseConstraintList(level) })
            || parseField(CabalTokelTypes.BUILD_TOOLS       , "build-tools"      , { parseConstraintList(level) })
            || parseField(CabalTokelTypes.BUILDABLE         , "buildable"        , { token(CabalTokelTypes.ID) })
            || parseField(CabalTokelTypes.EXTENSIONS        , "extensions"       , { parseIDList(level) })
            || parseField(CabalTokelTypes.OTHER_MODULES     , "other-modules"    , { parseIDList(level) })
            || parseField(CabalTokelTypes.HS_SOURCE_DIRS    , "hs-source-dirs"   , { parseDirectoryList(level) })

            || parseFieldList(CabalTokelTypes.OPTIONS_FIELD , OPTIONS_FIELD_NAMES, { parseIDList(level) })

    fun parseProperties(prevLevel: Int, isExecutable : Boolean): Boolean {
        var currentLevel : Int? = null;
        while (!builder.eof()) {
            val level = findLevel(currentLevel)
            if (level == null) {
                break;
            }
            if (currentLevel == null) {
                if (level > prevLevel) {
                    currentLevel = level
                } else {
                    return false;
                }
            }

            var result = false
            if (isExecutable) {
                result = parseMainFile()
            }
            result = result
                  || parseBuildInformation(currentLevel!!)
                  || parseProperty(currentLevel!!)
                  || parseIf(currentLevel!!, isExecutable)
                  || parseElse(currentLevel!!, isExecutable)

            if (!result) {
                builder.advanceLexer()
            }
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun parseIf(level: Int, isExecutable : Boolean) = start(CabalTokelTypes.PROPERTY) {
        val result = start(CabalTokelTypes.PROPERTY_KEY) { matchesIgnoreCase(CabalTokelTypes.ID, "if") }
        if (result) {
            while (!builder.eof()) {
                if (builder.getTokenType() == TokenType.NEW_LINE_INDENT) {
                    break
                }
                builder.advanceLexer()
            }
            parseProperties(level, isExecutable)
        } else {
            false
        }
    }

    fun parseElse(level: Int, isExecutable : Boolean) = start(CabalTokelTypes.PROPERTY) {
        var r = start(CabalTokelTypes.PROPERTY_KEY) { matchesIgnoreCase(CabalTokelTypes.ID, "else") }
        r = r && parseProperties(level, isExecutable);
        r
    }

    fun parseSectionType() = start(CabalTokelTypes.SECTION_TYPE) {
        token(CabalTokelTypes.ID);
    }

    fun parseExecutable(level: Int) = start(CabalTokelTypes.EXECUTABLE) {
        if (matchesIgnoreCase(CabalTokelTypes.ID, "executable")) {
            parseName() && parseProperties(level, true)
        } else {
            false
        }
    }

    fun parseTestSuite(level: Int) = start(CabalTokelTypes.TEST_SUITE) {
        if (matchesIgnoreCase(CabalTokelTypes.ID, "test-suite")) {
            token(CabalTokelTypes.ID) && parseProperties(level, false);
        } else {
            false
        }
    }

    fun parseSection(level: Int) =
            parseExecutable(level) ||
            parseTestSuite(level) ||
            start(CabalTokelTypes.SECTION) {
                val sections = listOf("source-repository", "flag")

                val result: Boolean = if (sections.contains(builder.getTokenText()?.toLowerCase())) {
                    parseSectionType() && token(CabalTokelTypes.ID)
                } else if (builder.getTokenText()?.toLowerCase() == "library") {
                    parseSectionType()
                } else {
                    false
                }
                if (result) {
                    parseProperties(level, false);
                }
                result
            }

    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()

        while (!builder.eof()) {
            if (!(parseTopLevelField() || parseProperty(0) || parseSection(0))) {
                builder.advanceLexer()
            }
        }

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

}