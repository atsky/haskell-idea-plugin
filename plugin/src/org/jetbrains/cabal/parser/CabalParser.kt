package org.jetbrains.haskell.cabal

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.psi.TokenType
import org.jetbrains.haskell.parser.rules.BaseParser


class CabalParser(root: IElementType, builder: PsiBuilder) : BaseParser(root, builder) {

    public fun parse(): ASTNode {
        return parseInternal(root)
    }

    fun parsePropertyKey() = start(CabalTokelTypes.PROPERTY_KEY) {
        token(CabalTokelTypes.ID)
    }

    fun parsePropertyKey(propName : String) = start(CabalTokelTypes.PROPERTY_KEY) {
        matchesIgnoreCase(CabalTokelTypes.ID, propName)
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

    fun parseProperty(level: Int) = start(CabalTokelTypes.PROPERTY) {
        var res = parsePropertyKey()
        res = res && token(CabalTokelTypes.COLON)
        res = res && parsePropertyValue(level)
        res
    }

    fun parseName() = start(CabalTokelTypes.NAME) {
        token(CabalTokelTypes.ID)
    }

    fun parseFileName() = start(CabalTokelTypes.FILE_NAME) {
        token(CabalTokelTypes.ID)
    }

    fun parseFileRef() = start(CabalTokelTypes.FILE_REF) {
        token(CabalTokelTypes.ID)
    }

    fun parseFileRefList() : Boolean {
        var res = parseFileRef()
        while ((!builder.eof()) && res) {
            if (builder.getTokenType() == TokenType.NEW_LINE_INDENT) {
                if (indentSize(builder.getTokenText()!!) == 0) {
                    break;
                }
                builder.advanceLexer()
            }
            else {
                res = parseFileRef()
            }
        }
        return res
    }

    fun parseSimpleVersionConstraint() = start(CabalTokelTypes.SIMPLE_CONSTRAINT) {
        token(CabalTokelTypes.COMPARATOR)
                && token(CabalTokelTypes.ID)
    }

    fun parseComplexVersionConstraint(prevLevel : Int = 0) = start(CabalTokelTypes.COMPLEX_CONSTRAINT) {
        parseSimpleVersionConstraint()
    }

    fun parseURL() = start(CabalTokelTypes.URL) {
        token(CabalTokelTypes.ID)
    }

    fun parseFullVersionConstraint(prevLevel: Int) = start(CabalTokelTypes.FULL_CONSTRAINT) {
        var res = token(CabalTokelTypes.ID)
        parseComplexVersionConstraint(prevLevel)
        res
    }

    fun parseDependensList(prevLevel: Int) : Boolean {
        var res = parseFullVersionConstraint(prevLevel)
        var isLast = false
        while ((!builder.eof()) && res && (!isLast)) {
            val marker = builder.mark()!!
            var wantNext = token(CabalTokelTypes.COMMA)

            while (wantNext && (!builder.eof()) && (builder.getTokenType() == TokenType.NEW_LINE_INDENT)) {
                if (indentSize(builder.getTokenText()!!) <= prevLevel) {
                    isLast = true
                    break;
                }
                builder.advanceLexer();
            }
            if ((!wantNext) || isLast) {
                marker.rollbackTo()
                break
            } else {
                res = parseFullVersionConstraint(prevLevel)
                marker.drop()
            }
        }
        return res
    }

    ///////////////////////////////////////////  global properties parsing  /////////////////////////////////////////////////

    fun parseNameField() = start(CabalTokelTypes.NAME_FIELD) {
        parsePropertyKey("name")
                && token(CabalTokelTypes.COLON)
                && parseName()
    }

    fun parseHomepage() = start(CabalTokelTypes.HOMEPAGE) {
        parsePropertyKey("homepage")
                && token(CabalTokelTypes.COLON)
                && parseURL()
    }

    fun parsePackageURL() = start(CabalTokelTypes.PACKAGE_URL) {
        var res = parsePropertyKey("package-url")
        res = res && token(CabalTokelTypes.COLON)
        res = res && parseURL()
        res
    }

    fun parseCabalVersionField() = start(CabalTokelTypes.CABAL_VERSION) {
        var res = parsePropertyKey("cabal-version")
        res = res && token(CabalTokelTypes.COLON)
        res = res && parseComplexVersionConstraint()
        res
    }

    fun parseVersionProperty() = start(CabalTokelTypes.VERSION) {
        var res = parsePropertyKey("version")
        res = res && token(CabalTokelTypes.COLON)
        res = res && token(CabalTokelTypes.ID)
        res
    }

    fun parseDataFiles() = start(CabalTokelTypes.DATA_FILES) {
        parsePropertyKey("data-files")
                && token(CabalTokelTypes.COLON)
                && parseFileRefList()
    }

    fun parseExtraSource() = start(CabalTokelTypes.EXTRA_SOURCE) {
        parsePropertyKey("extra-source-files")
                && token(CabalTokelTypes.COLON)
                && parseFileRefList()
    }

    fun parseExtraTmp() = start(CabalTokelTypes.EXTRA_TMP) {
        parsePropertyKey("extra-tmp-files")
                && token(CabalTokelTypes.COLON)
                && parseFileRefList()
    }

    fun parseExtraDoc() = start(CabalTokelTypes.EXTRA_DOC) {
        parsePropertyKey("extra-doc-files")
                && token(CabalTokelTypes.COLON)
                && parseFileRefList()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun parseMainFile(level: Int) = start(CabalTokelTypes.MAIN_FILE) {
        parsePropertyKey("main-is")
                && token(CabalTokelTypes.COLON)
                && parseFileName()
    }

    /////////////////////////////////////////////   build information parsing  //////////////////////////////////////////

    fun parseBuildDepends(level: Int) = start(CabalTokelTypes.BUILD_DEPENDS) {
        parsePropertyKey("build-depends")
                && token(CabalTokelTypes.COLON)
                && parseDependensList(level)
    }


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
                result = parseMainFile(prevLevel)
            }
            result = result
                  || parseBuildDepends(currentLevel!!)
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
            if (!(parseExtraDoc() || parseExtraDoc()          || parseExtraTmp()
                                  || parseDataFiles()
                                  || parseExtraSource()       ||parseVersionProperty()
                                  || parseCabalVersionField() || parseNameField()
                                  || parsePackageURL()        || parseHomepage()
                                  || parseProperty(0)         || parseSection(0))) {
                builder.advanceLexer()
            }
        }

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

}