package org.jetbrains.cabal.parser

import org.jetbrains.cabal.parser.*
import com.intellij.psi.tree.IElementType
import kotlin.Pair
import kotlin.MutableMap
import java.util.*

// https://github.com/ghc/packages-Cabal/blob/master/Cabal/Distribution/PackageDescription/Parse.hs

public val PKG_DESCR_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = mapOf(
    "name"               to Pair(CabalTokelTypes.NAME_FIELD        , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.NAME) }      ),
    "version"            to Pair(CabalTokelTypes.VERSION           , { CabalParser.(level: Int) -> this.parseVersionValue() }                     ),
    "cabal-version"      to Pair(CabalTokelTypes.CABAL_VERSION     , { CabalParser.(level: Int) -> this.parseSimpleVersionConstraint() }          ),
    "build-type"         to Pair(CabalTokelTypes.BUILD_TYPE_FIELD  , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.BUILD_TYPE) }),
    "license"            to Pair(CabalTokelTypes.LICENSE           , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.IDENTIFIER) }),
    "license-file"       to Pair(CabalTokelTypes.LICENSE_FILES     , { CabalParser.(level: Int) -> this.parsePath() }                             ),
    "copyright"          to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                                                   ),
    "author"             to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                                                   ),
    "maintainer"         to Pair(CabalTokelTypes.SINGLE_VAL        , { CabalParser.(level: Int) -> this.parseFreeLine(CabalTokelTypes.E_MAIL) }   ),
    "stability"          to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                                                   ),
    "homepage"           to Pair(CabalTokelTypes.SINGLE_VAL        , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.URL) }    ),
    "bug-reports"        to Pair(CabalTokelTypes.SINGLE_VAL        , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.URL) }    ),
    "package-url"        to Pair(CabalTokelTypes.SINGLE_VAL        , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.URL) }    ),
    "synopsis"           to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                                                   ),
    "description"        to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                                                   ),
    "category"           to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                                                   ),

    "tested-with"        to Pair(CabalTokelTypes.TESTED_WITH       , CabalParser::parseCompilerList                                               ),

    "data-files"         to Pair(CabalTokelTypes.DATA_FILES        , CabalParser::parsePathList                                                   ),
    "data-dir"           to Pair(CabalTokelTypes.DATA_DIR          , { CabalParser.(level: Int) -> this.parsePath() }                             ),
    "extra-source-files" to Pair(CabalTokelTypes.EXTRA_SOURCE      , CabalParser::parsePathList                                                   ),
    "extra-tmp-files"    to Pair(CabalTokelTypes.EXTRA_TMP         , CabalParser::parsePathList                                                   )
)

public val BUILD_INFO_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = mapOf(
    "build-depends"      to Pair(CabalTokelTypes.BUILD_DEPENDS     , { CabalParser.(level: Int) -> this.parseConstraintList(level) }             ),
    "other-modules"      to Pair(CabalTokelTypes.OTHER_MODULES     , CabalParser::parseIdList                                                    ),
    "hs-source-dirs"     to Pair(CabalTokelTypes.HS_SOURCE_DIRS    , CabalParser::parsePathList                                                  ),
    "hs-source-dir"      to Pair(CabalTokelTypes.HS_SOURCE_DIRS    , { CabalParser.(level: Int) -> this.parsePath() }                            ),
    "extensions"         to Pair(CabalTokelTypes.EXTENSIONS        , CabalParser::parseIdList                                                    ),
    "build-tools"        to Pair(CabalTokelTypes.BUILD_TOOLS       , { CabalParser.(level: Int) -> this.parseConstraintList(level) }             ),
    "buildable"          to Pair(CabalTokelTypes.BUILDABLE         , { CabalParser.(level: Int) -> this.parseBool() }                            ),

    "ghc-options"        to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "ghc-prof-options"   to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "ghc-shared-options" to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "hugs-options"       to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "nhc98-options"      to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "jhc-options"        to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),

    "includes"           to Pair(CabalTokelTypes.INCLUDES          , CabalParser::parsePathList                                                  ),
    "install-includes"   to Pair(CabalTokelTypes.INSTALL_INCLUDES  , CabalParser::parsePathList                                                  ),
    "include-dirs"       to Pair(CabalTokelTypes.INCLUDE_DIRS      , CabalParser::parsePathList                                                  ),

    "c-sources"          to Pair(CabalTokelTypes.C_SOURCES         , CabalParser::parsePathList                                                  ),

    "extra-libraries"    to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseTokenList                                                 ),
    "extra-lib-dirs"     to Pair(CabalTokelTypes.EXTRA_LIB_DIRS    , CabalParser::parsePathList                                                  ),

    "cc-options"         to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "cpp-options"        to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),
    "ld-options"         to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseOptionList                                                ),

    "pkgconfig-depends"  to Pair(CabalTokelTypes.PKG_CONFIG_DEPENDS, { CabalParser.(level: Int) -> this.parseConstraintList(level) }             ),
    "frameworks"         to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseTokenList                                                 ),

    "default-extensions" to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseIdList                                                    ),
    "other-extensions"   to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseIdList                                                    ),
    "default-language"   to Pair(CabalTokelTypes.SINGLE_VAL        , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.LANGUAGE) } ),
    "other-languages"    to Pair(CabalTokelTypes.MULTI_VAL         , CabalParser::parseLanguageList                                              )
)

public val LIBRARY_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = {
    val res = hashMapOf(
            "exposed-modules"    to Pair(CabalTokelTypes.EXPOSED_MODULES, CabalParser::parseIdList                        ),
            "exposed"            to Pair(CabalTokelTypes.EXPOSED        , { CabalParser.(level: Int) -> this.parseBool() })
    )
    res.putAll(BUILD_INFO_FIELDS)
    res
}()

public val EXECUTABLE_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = {
    var res = hashMapOf(
        "main-is"            to Pair(CabalTokelTypes.MAIN_FILE         , { CabalParser.(level: Int) -> this.parsePath() })
    )
    res.putAll(BUILD_INFO_FIELDS)
    res
}()

public val TEST_SUITE_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = {
    var res = hashMapOf(
        "type"               to Pair(CabalTokelTypes.TYPE              , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.TEST_SUITE_TYPE) }),
        "main-is"            to Pair(CabalTokelTypes.MAIN_FILE         , { CabalParser.(level: Int) -> this.parsePath() }                                  ),
        "test-module"        to Pair(CabalTokelTypes.TEST_MODULE       , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.IDENTIFIER) }     )
    )
    res.putAll(BUILD_INFO_FIELDS)
    res
}()

public val BENCHMARK_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = {
    var res = hashMapOf(
        "type"               to Pair(CabalTokelTypes.TYPE              , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.BENCHMARK_TYPE) } ),
        "main-is"            to Pair(CabalTokelTypes.MAIN_FILE         , { CabalParser.(level: Int) -> this.parsePath() }                                  )
    )
    res.putAll(BUILD_INFO_FIELDS)
    res
}()

public val FLAG_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = mapOf(
    "description"        to Pair(CabalTokelTypes.SINGLE_VAL        , CabalParser::parseFreeForm                      ),
    "default"            to Pair(CabalTokelTypes.BOOL_FIELD        , { CabalParser.(level: Int) -> this.parseBool() }),
    "manual"             to Pair(CabalTokelTypes.BOOL_FIELD        , { CabalParser.(level: Int) -> this.parseBool() })
)

public val SOURCE_REPO_FIELDS: Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>> = mapOf(
    "location"           to Pair(CabalTokelTypes.REPO_LOCATION     , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.URL) }   ),
    "type"               to Pair(CabalTokelTypes.TYPE              , { CabalParser.(level: Int) -> this.parseIdValue(CabalTokelTypes.REPO_TYPE) }),
    "subdir"             to Pair(CabalTokelTypes.REPO_SUBDIR       , { CabalParser.(level: Int) -> this.parsePath() }                            ),
    "module"             to Pair(CabalTokelTypes.REPO_MODULE       , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.TOKEN) } ),
    "tag"                to Pair(CabalTokelTypes.REPO_TAG          , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.TOKEN) } ),
    "branch"             to Pair(CabalTokelTypes.SINGLE_VAL        , { CabalParser.(level: Int) -> this.parseTokenValue(CabalTokelTypes.TOKEN) } )
)

public val SECTION_TYPES: Map<String, IElementType> = mapOf(
        "flag"                  to CabalTokelTypes.FLAG          ,
        "executable"            to CabalTokelTypes.EXECUTABLE    ,
        "library"               to CabalTokelTypes.LIBRARY       ,
        "test-suite"            to CabalTokelTypes.TEST_SUITE    ,
        "benchmark"             to CabalTokelTypes.BENCHMARK     ,
        "if"                    to CabalTokelTypes.IF_CONDITION  ,
        "else"                  to CabalTokelTypes.ELSE_CONDITION,
        "source-repository"     to CabalTokelTypes.SOURCE_REPO
)

public val SECTIONS: Map<String, Pair< CabalParser.(Int) -> Boolean,
                                       Map<String, Pair<IElementType, CabalParser.(Int) -> Boolean>>?
                                                                                                        >> = mapOf(
        "executable"            to Pair({ CabalParser.(level: Int) -> this.parseSectionName() }, EXECUTABLE_FIELDS) ,
        "library"               to Pair({ CabalParser.(level: Int) -> true }                   , LIBRARY_FIELDS)    ,
        "test-suite"            to Pair({ CabalParser.(level: Int) -> this.parseSectionName() }, TEST_SUITE_FIELDS) ,
        "benchmark"             to Pair({ CabalParser.(level: Int) -> this.parseSectionName() }, BENCHMARK_FIELDS)  ,
        "flag"                  to Pair({ CabalParser.(level: Int) -> this.parseSectionName() }, FLAG_FIELDS)       ,
        "source-repository"     to Pair({ CabalParser.(level: Int) -> this.parseRepoKinds() }  , SOURCE_REPO_FIELDS),
        "if"                    to Pair(CabalParser::parseFullCondition                        , null)              ,
        "else"                  to Pair({ CabalParser.(level: Int) -> true }                   , null)
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

