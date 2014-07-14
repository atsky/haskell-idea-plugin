package org.jetbrains.haskell.cabal

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.cabal.psi.VersionProperty
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
        var r = parsePropertyKey()
        r = r && token(CabalTokelTypes.COLON)
        r = r && parsePropertyValue(level)
        r
    }

    fun parseSimpleVersionConstraint(prevLevel: Int) = start(CabalTokelTypes.SIMPLE_CONSTRAINT) {
        var res = token(CabalTokelTypes.ID)
        res = res && token(CabalTokelTypes.ID)
        res
    }

    fun parseCabalVersionField(level: Int) = start(CabalTokelTypes.CABAL_VERSION) {
        var res = parsePropertyKey("cabal-version")
        res = res && token(CabalTokelTypes.COLON)
        res = res && parseSimpleVersionConstraint(level)
        res
    }

    fun parseVersionProperty(level: Int) = start(CabalTokelTypes.VERSION) {
        var r = parsePropertyKey("version")
        r = r && token(CabalTokelTypes.COLON)
        r = r && parsePropertyValue(level)
        r
    }

    fun parseIf(level: Int) = start(CabalTokelTypes.PROPERTY) {
        val result = start(CabalTokelTypes.PROPERTY_KEY) { matchesIgnoreCase(CabalTokelTypes.ID, "if") }
        if (result) {
            while (!builder.eof()) {
                if (builder.getTokenType() == TokenType.NEW_LINE_INDENT) {
                    break
                }
                builder.advanceLexer()
            }
            parseProperties(level)
        } else {
            false
        }
    }

    fun parseElse(level: Int) = start(CabalTokelTypes.PROPERTY) {
        var r = start(CabalTokelTypes.PROPERTY_KEY) { matchesIgnoreCase(CabalTokelTypes.ID, "else") }
        r = r && parseProperties(level);
        r
    }

    fun parseSectionType() = start(CabalTokelTypes.SECTION_TYPE) {
        token(CabalTokelTypes.ID);
    }

    fun parseProperties(prevLevel: Int): Boolean {
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

            var result = parseVersionProperty(currentLevel!!) || parseCabalVersionField(currentLevel!!)
            result = result || parseProperty(currentLevel!!)
            result = result || parseIf(currentLevel!!)
            result = result || parseElse(currentLevel!!)
            if (!result) {
                builder.advanceLexer()
            }
        }
        return true;
    }

    fun parseName() = start(CabalTokelTypes.NAME) {
        token(CabalTokelTypes.ID)
    }

    fun parseExecutable(level: Int) = start(CabalTokelTypes.EXECUTABLE) {
        if (matchesIgnoreCase(CabalTokelTypes.ID, "executable")) {
            parseName() && parseProperties(level)
        } else {
            false
        }
    }

    fun parseTestSuite(level: Int) = start(CabalTokelTypes.TEST_SUITE) {
        if (matchesIgnoreCase(CabalTokelTypes.ID, "test-suite")) {
            token(CabalTokelTypes.ID) && parseProperties(level);
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
                    parseProperties(level);
                }
                result
            }

    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()

        while (!builder.eof()) {
            if (!(parseVersionProperty(0) || parseCabalVersionField(0) || parseProperty(0) || parseSection(0))) {
                builder.advanceLexer()
            }
        }

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

}