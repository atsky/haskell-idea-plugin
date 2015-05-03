package org.jetbrains.cabal.parser

import org.jetbrains.cabal.parser.*
import com.intellij.psi.tree.IElementType
import kotlin.Pair
import kotlin.MutableMap
import java.util.*
import org.jetbrains.cabal.parser.CabalTokelTypes as CT

class FieldsBuilder {
    val map : MutableMap<String, Pair<IElementType, ExtensionFunction1<CabalParser, Int, Boolean>>> =
        HashMap()

    fun field(name : String , elementType : IElementType, f : CabalParser.(Int) -> Boolean) {
        map.put(name, Pair(elementType, f))
    }

    fun addAll(fields : Map<String, Pair<IElementType, ExtensionFunction1<CabalParser, Int, Boolean>>>) {
        map.putAll(fields)
    }

    fun build(): MutableMap<String, Pair<IElementType, ExtensionFunction1<CabalParser, Int, Boolean>>> {
        return map
    }

}

fun makeFields(body : FieldsBuilder.() -> Unit) : Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> {
    val builder = FieldsBuilder()
    builder.body()
    return builder.build()
}
// https://github.com/ghc/packages-Cabal/blob/master/Cabal/Distribution/PackageDescription/Parse.hs

public val PKG_DESCR_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("name",               CT.NAME_FIELD,       { parseIdValue(CT.NAME) })
    field("version",            CT.VERSION,          { parseVersionValue() } )
    field("cabal-version",      CT.CABAL_VERSION,    { parseSimpleVersionConstraint() })
    field("build-type",         CT.BUILD_TYPE_FIELD, { parseIdValue(CT.BUILD_TYPE) })
    field("license",            CT.LICENSE,          { parseIdValue(CT.IDENTIFIER) })
    field("license-file",       CT.LICENSE_FILES,    { parsePath() })
    field("copyright",          CT.SINGLE_VAL,       CabalParser::parseFreeForm)
    field("author",             CT.SINGLE_VAL,       CabalParser::parseFreeForm)
    field("maintainer",         CT.SINGLE_VAL,       { parseFreeLine(CT.E_MAIL) })
    field("stability",          CT.SINGLE_VAL,       CabalParser::parseFreeForm)
    field("homepage",           CT.SINGLE_VAL,       { parseTokenValue(CT.URL) })
    field("bug-reports",        CT.SINGLE_VAL,       { parseTokenValue(CT.URL) })
    field("package-url",        CT.SINGLE_VAL,       { parseTokenValue(CT.URL) })
    field("synopsis",           CT.SINGLE_VAL,       CabalParser::parseFreeForm)
    field("description",        CT.SINGLE_VAL,       CabalParser::parseFreeForm)
    field("category",           CT.SINGLE_VAL,       CabalParser::parseFreeForm)

    field("tested-with",        CT.TESTED_WITH,      CabalParser::parseCompilerList)

    field("data-files",         CT.DATA_FILES,       CabalParser::parsePathList)
    field("data-dir",           CT.DATA_DIR,         { parsePath() })
    field("extra-source-files", CT.EXTRA_SOURCE,     CabalParser::parsePathList)
    field("extra-tmp-files",    CT.EXTRA_TMP,        CabalParser::parsePathList)
}

public val BUILD_INFO_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("build-depends",  CT.BUILD_DEPENDS,  { l -> parseConstraintList(l) })
    field("other-modules",  CT.OTHER_MODULES,  CabalParser::parseIdList)
    field("hs-source-dirs", CT.HS_SOURCE_DIRS, CabalParser::parsePathList)
    field("hs-source-dir",  CT.HS_SOURCE_DIRS, { parsePath() })
    field("extensions",     CT.EXTENSIONS,     CabalParser::parseIdList)
    field("build-tools",    CT.BUILD_TOOLS,    { l -> parseConstraintList(l) })
    field("buildable",      CT.BUILDABLE,      { parseBool() })

    field("ghc-options",        CT.MULTI_VAL, CabalParser::parseOptionList)
    field("ghc-prof-options",   CT.MULTI_VAL, CabalParser::parseOptionList)
    field("ghc-shared-options", CT.MULTI_VAL, CabalParser::parseOptionList)
    field("hugs-options",       CT.MULTI_VAL, CabalParser::parseOptionList)
    field("nhc98-options",      CT.MULTI_VAL, CabalParser::parseOptionList)
    field("jhc-options",        CT.MULTI_VAL, CabalParser::parseOptionList)

    field("includes",         CT.INCLUDES, CabalParser::parsePathList)
    field("install-includes", CT.INSTALL_INCLUDES, CabalParser::parsePathList)
    field("include-dirs",     CT.INCLUDE_DIRS, CabalParser::parsePathList)

    field("c-sources", CT.C_SOURCES, CabalParser::parsePathList)

    field("extra-libraries", CT.MULTI_VAL, CabalParser::parseTokenList)
    field("extra-lib-dirs",  CT.EXTRA_LIB_DIRS, CabalParser::parsePathList)

    field("cc-options",  CT.MULTI_VAL, CabalParser::parseOptionList)
    field("cpp-options", CT.MULTI_VAL, CabalParser::parseOptionList)
    field("ld-options",  CT.MULTI_VAL, CabalParser::parseOptionList)

    field("pkgconfig-depends", CT.PKG_CONFIG_DEPENDS, { l -> parseConstraintList(l) })
    field("frameworks",        CT.MULTI_VAL, CabalParser::parseTokenList)

    field("default-extensions", CT.MULTI_VAL, CabalParser::parseIdList)
    field("other-extensions",   CT.MULTI_VAL, CabalParser::parseIdList)
    field("default-language",   CT.SINGLE_VAL, { parseIdValue(CT.LANGUAGE) })
    field("other-languages",    CT.MULTI_VAL, CabalParser::parseLanguageList)
}

public val LIBRARY_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("exposed-modules", CT.EXPOSED_MODULES, CabalParser::parseIdList)
    field("exposed", CT.EXPOSED, { parseBool() })
    addAll(BUILD_INFO_FIELDS)
}

public val EXECUTABLE_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("main-is", CT.MAIN_FILE, { parsePath() } )
    addAll(BUILD_INFO_FIELDS)
}


public val TEST_SUITE_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("type",        CT.TYPE,  { parseIdValue(CT.TEST_SUITE_TYPE) })
    field("main-is",     CT.MAIN_FILE, { parsePath() })
    field("test-module", CT.TEST_MODULE, { parseIdValue(CT.IDENTIFIER) })
    addAll(BUILD_INFO_FIELDS)
}


public val BENCHMARK_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("type",    CT.TYPE,      { parseIdValue(CT.BENCHMARK_TYPE) })
    field("main-is", CT.MAIN_FILE, { parsePath() })
    addAll(BUILD_INFO_FIELDS)
}


public val FLAG_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("description", CT.SINGLE_VAL, CabalParser::parseFreeForm)
    field("default",     CT.BOOL_FIELD, { parseBool() })
    field("manual",      CT.BOOL_FIELD, { parseBool() })
}

public val SOURCE_REPO_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = makeFields {
    field("location", CT.REPO_LOCATION, { parseTokenValue(CT.URL) })
    field("type",     CT.TYPE,          { parseIdValue(CT.REPO_TYPE) })
    field("subdir",   CT.REPO_SUBDIR,   { parsePath() })
    field("module",   CT.REPO_MODULE,   { parseTokenValue(CT.TOKEN) })
    field("tag",      CT.REPO_TAG,      { parseTokenValue(CT.TOKEN) })
    field("branch",   CT.SINGLE_VAL,    { parseTokenValue(CT.TOKEN) })
}

public val SECTION_TYPES: Map<String, IElementType> = mapOf(
        "flag"                  to CT.FLAG          ,
        "executable"            to CT.EXECUTABLE    ,
        "library"               to CT.LIBRARY       ,
        "test-suite"            to CT.TEST_SUITE    ,
        "benchmark"             to CT.BENCHMARK     ,
        "if"                    to CT.IF_CONDITION  ,
        "else"                  to CT.ELSE_CONDITION,
        "source-repository"     to CT.SOURCE_REPO
)

private fun parseFun(f : CabalParser.(Int) -> Boolean) = f

public val SECTIONS: Map<String, Pair< CabalParser.(Int) -> Boolean,
                                       Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>>?
                                                                                                        >> = mapOf(
        "executable"            to Pair(parseFun { parseSectionName() },     EXECUTABLE_FIELDS) ,
        "library"               to Pair(parseFun { true },                   LIBRARY_FIELDS)    ,
        "test-suite"            to Pair(parseFun { this.parseSectionName()}, TEST_SUITE_FIELDS) ,
        "benchmark"             to Pair(parseFun { this.parseSectionName()}, BENCHMARK_FIELDS)  ,
        "flag"                  to Pair(parseFun { this.parseSectionName()}, FLAG_FIELDS)       ,
        "source-repository"     to Pair(parseFun { this.parseRepoKinds()},   SOURCE_REPO_FIELDS),
        "if"                    to Pair(CabalParser::parseFullCondition,     null)              ,
        "else"                  to Pair(parseFun { true},                    null)
)

public val TOP_SECTION_NAMES: List<String> = listOf(
        "flag",
        "executable",
        "library",
        "test-suite",
        "benchmark",
        "source-repository"
)

public val IF_ELSE: List<String> = listOf(
        "if",
        "else"
)

public val BUILD_INFO_SECTIONS: List<String> = listOf(
        "executable",
        "library",
        "test-suite",
        "benchmark"
)

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public val REPO_KIND_VALS : List<String> = listOf(
        "this",
        "head"
)

public val REPO_TYPE_VALS : List<String> = listOf(
        "darcs",
        "git",
        "svn",
        "cvs",
        "mercurial",
        "hg",
        "bazaar",
        "bzr",
        "arch",
        "monotone"
)

public val TS_TYPE_VALS : List<String> = listOf(
        "exitcode-stdio-1.0",
        "detailed-0.9",
        "detailed-1.0"
)

public val BENCH_TYPE_VALS: List<String> = listOf(
        "exitcode-stdio-1.0"
)

public val BUILD_TYPE_VALS : List<String> = listOf(
        "Simple",
        "Configure",
        "Custom",
        "Make"
)

public val BOOL_VALS : List<String> = listOf(
        "true",
        "True",
        "false",
        "False"
)

public val COMPILER_VALS : List<String> = listOf(
        "ghc",
        "nhc",
        "yhc",
        "hugs",
        "hbc",
        "helium",
        "jhc",
        "lhc"
)

public val LANGUAGE_VALS : List<String> = listOf(
        "Haskell98",
        "Haskell2010"
)

