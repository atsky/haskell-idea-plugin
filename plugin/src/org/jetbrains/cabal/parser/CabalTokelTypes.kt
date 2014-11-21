package org.jetbrains.cabal.parser

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.CabalCompositeElementType
import org.jetbrains.haskell.parser.HaskellTokenType
import org.jetbrains.cabal.psi.*

public trait CabalTokelTypes {

    class object {
        val defaultContructor : (ASTNode) -> PsiElement = { node ->
            ASTWrapperPsiElement(node)
        }

        val COLON                  : IElementType  = HaskellTokenType(":")
        val COMMA                  : IElementType  = HaskellTokenType(",")
        val COMMENT                : IElementType  = HaskellTokenType("COMMENT")
        val OPEN_PAREN             : IElementType  = HaskellTokenType("(")
        val CLOSE_PAREN            : IElementType  = HaskellTokenType(")")
        val SLASH                  : IElementType  = HaskellTokenType("/")
        val END_OF_LINE_COMMENT    : IElementType  = HaskellTokenType("--")
        val STRING                 : IElementType  = HaskellTokenType("string")
        val NUMBER                 : IElementType  = HaskellTokenType("number")
        val ID                     : IElementType  = HaskellTokenType("id")
        val COMPARATOR             : IElementType  = HaskellTokenType("COMPARATOR")
        val LOGIC                  : IElementType  = HaskellTokenType("LOGIC")
        val EQ                     : IElementType  = HaskellTokenType("=")
        val AND                    : IElementType  = HaskellTokenType("&")
        val OR                     : IElementType  = HaskellTokenType("|")
        val OPEN_CURLY             : IElementType  = HaskellTokenType("{")
        val CLOSE_CURLY            : IElementType  = HaskellTokenType("}")
        val TAB                    : IElementType  = HaskellTokenType("TAB")
        val NEGATION               : IElementType  = HaskellTokenType("!")

        val COMMENTS               : TokenSet = TokenSet.create(END_OF_LINE_COMMENT, COMMENT)
        val WHITESPACES            : TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

        val PROPERTY_KEY           : IElementType = CabalCompositeElementType("PROPERTY_KEY", ::PropertyKey)
        val SECTION                : IElementType = CabalCompositeElementType("SECTION", defaultContructor)

        val SECTION_TYPE           : IElementType = CabalCompositeElementType("SECTION_TYPE"             , ::SectionType              )

        val EXECUTABLE             : IElementType = CabalCompositeElementType("EXECUTABLE"               , ::Executable               )
        val LIBRARY                : IElementType = CabalCompositeElementType("LIBRARY"                  , ::Library                  )
        val BENCHMARK              : IElementType = CabalCompositeElementType("BENCHMARK"                , ::Benchmark                )
        val TEST_SUITE             : IElementType = CabalCompositeElementType("TEST_SUITE"               , ::TestSuite                )
        val SOURCE_REPO            : IElementType = CabalCompositeElementType("SOURCE_REPO"              , ::SourceRepo               )
        val FLAG                   : IElementType = CabalCompositeElementType("FLAG"                     , ::Flag                     )
        val IF_CONDITION           : IElementType = CabalCompositeElementType("IF_CONDITION"             , ::IfCondition              )
        val ELSE_CONDITION         : IElementType = CabalCompositeElementType("ELSE_CONDITION"           , ::ElseCondition            )

        val VERSION                : IElementType = CabalCompositeElementType("VERSION_PROPERTY"         , ::VersionField             )
        val CABAL_VERSION          : IElementType = CabalCompositeElementType("CABAL_VERSION_PROPERTY"   , ::CabalVersionField        )
        val NAME_FIELD             : IElementType = CabalCompositeElementType("NAME_FIELD"               , ::NameField                )
        val BUILDABLE              : IElementType = CabalCompositeElementType("BUILDABLE"                , ::BuildableField           )
        val EXTENSIONS             : IElementType = CabalCompositeElementType("EXTENSIONS"               , ::ExtensionsField          )
        val OTHER_MODULES          : IElementType = CabalCompositeElementType("OTHER_MODULES"            , ::OtherModulesField        )
        val DATA_DIR               : IElementType = CabalCompositeElementType("DATA_DIR"                 , ::DataDirField             )
        val BUILD_TYPE_FIELD       : IElementType = CabalCompositeElementType("BUILD_TYPE"               , ::BuildTypeField           )
        val LICENSE                : IElementType = CabalCompositeElementType("LICENSE"                  , ::LicenseField             )
        val TESTED_WITH            : IElementType = CabalCompositeElementType("TESTED_WITH"              , ::TestedWithField          )
        val LICENSE_FILES          : IElementType = CabalCompositeElementType("LICENSE_FILES"            , ::LicenseFilesField        )
        val TEST_MODULE            : IElementType = CabalCompositeElementType("TEST_MODULE"              , ::TestModuleField          )
        val TYPE                   : IElementType = CabalCompositeElementType("TYPE"                     , ::TypeField                )
        val HS_SOURCE_DIRS         : IElementType = CabalCompositeElementType("HS_SOURCE_DIRS"           , ::HsSourceDirsField        )
        val INCLUDE_DIRS           : IElementType = CabalCompositeElementType("INCLUDE_DIRS"             , ::IncludeDirsField         )
        val BOOL_FIELD             : IElementType = CabalCompositeElementType("BOOL_FIELD"               , ::BoolField                )
        val C_SOURCES              : IElementType = CabalCompositeElementType("C_SOURCES"                , ::CSourcesField            )
        val EXTRA_SOURCE           : IElementType = CabalCompositeElementType("EXTRA_SOURCE"             , ::ExtraSourceField         )
        val EXTRA_TMP              : IElementType = CabalCompositeElementType("EXTRA_TMP"                , ::ExtraTmpField            )
        val EXTRA_LIB_DIRS         : IElementType = CabalCompositeElementType("EXTRA_LIB_DIRS"           , ::ExtraLibDirsField        )
        val DATA_FILES             : IElementType = CabalCompositeElementType("DATA_FILES"               , ::DataFilesField           )
        val INSTALL_INCLUDES       : IElementType = CabalCompositeElementType("INSTALL_INCLUDES"         , ::InstallIncludesField     )
        val INCLUDES               : IElementType = CabalCompositeElementType("INCLUDES"                 , ::IncludesField            )
        val INVALID_FIELD          : IElementType = CabalCompositeElementType("INVALID_FIELD"            , ::InvalidField             )
        val MULTI_VAL              : IElementType = CabalCompositeElementType("MULTI_VAL"                , ::MultiValueField          )
        val SINGLE_VAL             : IElementType = CabalCompositeElementType("SINGLE_VAL"               , ::SingleValueField         )

        val MAIN_FILE              : IElementType = CabalCompositeElementType("MAIN_FILE"                , ::MainFileField            )

        val EXPOSED_MODULES        : IElementType = CabalCompositeElementType("EXPOSED_MODULES"          , ::ExposedModulesField      )
        val EXPOSED                : IElementType = CabalCompositeElementType("EXPOSED"                  , ::ExposedField             )

        val BUILD_DEPENDS          : IElementType = CabalCompositeElementType("BUILD_DEPENDS"            , ::BuildDependsField        )
        val PKG_CONFIG_DEPENDS     : IElementType = CabalCompositeElementType("PKG_CONFIG_DEPENDS"       , ::PkgConfigDependsField    )
        val BUILD_TOOLS            : IElementType = CabalCompositeElementType("BUILD_TOOLS"              , ::BuildToolsField          )

        val VERSION_CONSTRAINT     : IElementType = CabalCompositeElementType("VERSION_CONSTRAINT"       , ::VersionConstraint        )
        val COMPLEX_CONSTRAINT     : IElementType = CabalCompositeElementType("COMPLEX_CONSTRAINT"       , ::ComplexVersionConstraint )
        val FULL_CONSTRAINT        : IElementType = CabalCompositeElementType("FULL_CONSTRAINT"          , ::FullVersionConstraint    )

        val FULL_CONDITION         : IElementType = CabalCompositeElementType("FULL_CONDITION"           , ::FullCondition            )
        val SIMPLE_CONDITION       : IElementType = CabalCompositeElementType("SIMPLE_CONDITION"         , ::SimpleCondition          )
        val CONDITION_PART         : IElementType = CabalCompositeElementType("CONDITION_PART"           , ::ConditionPart            )

        val URL                    : IElementType = CabalCompositeElementType("URL"                      , ::Url                      )
        val NAME                   : IElementType = CabalCompositeElementType("NAME"                     , ::Name                     )
        val PATH                   : IElementType = CabalCompositeElementType("PATH"                     , ::Path                     )
        val FREE_FORM              : IElementType = CabalCompositeElementType("FREE_FORM"                , ::FreeForm                 )
        val VERSION_VALUE          : IElementType = CabalCompositeElementType("VERSION_VALUE"            , ::VersionValue             )
        val IDENTIFIER             : IElementType = CabalCompositeElementType("IDENTIFIER"               , ::Identifier               )
        val E_MAIL                 : IElementType = CabalCompositeElementType("E_MAIL"                   , ::EMail                    )
        val TOKEN                  : IElementType = CabalCompositeElementType("TOKEN"                    , ::Token                    )
        val OPTION                 : IElementType = CabalCompositeElementType("OPTION"                   , ::Option                   )
        val COMPILER               : IElementType = CabalCompositeElementType("COMPILER"                 , ::CompilerId               )
        val LANGUAGE               : IElementType = CabalCompositeElementType("LANGUAGE"                 , ::Language                 )

        val INVALID_VALUE          : IElementType = CabalCompositeElementType("INVALID_VALUE"            , ::InvalidValue             )
        val INVALID_CONDITION_PART : IElementType = CabalCompositeElementType("INVALID_CONDITION_PART"   , ::InvalidConditionPart     )

        val TEST_SUITE_TYPE        : IElementType = CabalCompositeElementType("TEST_SUITE_TYPE"          , ::TestSuiteType            )
        val BENCHMARK_TYPE         : IElementType = CabalCompositeElementType("BENCHMARK_TYPE"           , ::BenchmarkType            )
        val BUILD_TYPE             : IElementType = CabalCompositeElementType("BUILD_TYPE"               , ::BuildType                )

        val REPO_SUBDIR            : IElementType = CabalCompositeElementType("REPO_SUBDIR"              , ::RepoSubdirField          )
        val REPO_LOCATION          : IElementType = CabalCompositeElementType("REPO_LOCATION"            , ::RepoLocationField        )
        val REPO_TAG               : IElementType = CabalCompositeElementType("REPO_TAG"                 , ::RepoTagField             )
        val REPO_MODULE            : IElementType = CabalCompositeElementType("REPO_MODULE"              , ::RepoModuleField          )

        val REPO_KIND              : IElementType = CabalCompositeElementType("REPO_KIND"                , ::RepoKind                 )
        val REPO_TYPE              : IElementType = CabalCompositeElementType("REPO_TYPE"                , ::RepoType                 )
    }
}
