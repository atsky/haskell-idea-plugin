package org.jetbrains.cabal.parser

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.CabalCompositeElementType
import org.jetbrains.haskell.parser.HaskellToken
import org.jetbrains.cabal.psi.*

public trait CabalTokelTypes {


    class object {
        val defaultContructor : (ASTNode) -> PsiElement = { node ->
            ASTWrapperPsiElement(node)
        }

        val COLON              : IElementType  = HaskellToken(":")
        val COMMA              : IElementType  = HaskellToken(",")
        val COMMENT            : IElementType  = HaskellToken("COMMENT")
        val OPEN_PAREN         : IElementType  = HaskellToken("(")
        val CLOSE_PAREN        : IElementType  = HaskellToken(")")
        val SLASH              : IElementType  = HaskellToken("/")
        val DOT                : IElementType  = HaskellToken(".")
        val END_OF_LINE_COMMENT: IElementType? = HaskellToken("--")
        val STRING             : IElementType  = HaskellToken("string")
        val NUMBER             : IElementType  = HaskellToken("number")
        val ID                 : IElementType  = HaskellToken("id")
        val COMPARATOR         : IElementType  = HaskellToken("COMPARATOR")
        val EQUALITY           : IElementType  = HaskellToken("==")
        val LOGIC              : IElementType  = HaskellToken("LOGIC")
        val EQ                 : IElementType  = HaskellToken("=")
        val AND                : IElementType  = HaskellToken("&")
        val OR                 : IElementType  = HaskellToken("|")
        val OPEN_CURLY         : IElementType  = HaskellToken("{")
        val CLOSE_CURLY        : IElementType  = HaskellToken("}")

        val COMMENTS     : TokenSet = TokenSet.create(END_OF_LINE_COMMENT, COMMENT)
        val WHITESPACES  : TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

        val PROPERTY           : IElementType = CabalCompositeElementType("PROPERTY", defaultContructor)
        val PROPERTY_KEY       : IElementType = CabalCompositeElementType("PROPERTY_KEY", ::PropertyKey)
        val PROPERTY_VALUE     : IElementType = CabalCompositeElementType("PROPERTY_VALUE" , defaultContructor)
        val SECTION            : IElementType = CabalCompositeElementType("SECTION", defaultContructor)

        val SECTION_TYPE       : IElementType = CabalCompositeElementType("SECTION_TYPE"             , ::SectionType             )

        val EXECUTABLE         : IElementType = CabalCompositeElementType("EXECUTABLE"               , ::Executable              )
        val LIBRARY            : IElementType = CabalCompositeElementType("LIBRARY"                  , ::Library                 )
        val BENCHMARK          : IElementType = CabalCompositeElementType("BENCHMARK"                , ::Benchmark               )
        val TEST_SUITE         : IElementType = CabalCompositeElementType("TEST_SUITE"               , ::TestSuite               )
        val SOURCE_REPO        : IElementType = CabalCompositeElementType("SOURCE_REPO"              , ::SourceRepo              )
        val FLAG               : IElementType = CabalCompositeElementType("FLAG"                     , ::Flag                    )
        val IF_CONDITION       : IElementType = CabalCompositeElementType("IF_CONDITION"             , ::IfCondition             )
        val ELSE_CONDITION     : IElementType = CabalCompositeElementType("ELSE_CONDITION"           , ::ElseCondition           )

        val VERSION            : IElementType = CabalCompositeElementType("VERSION_PROPERTY"         , ::VersionField            )
        val CABAL_VERSION      : IElementType = CabalCompositeElementType("CABAL_VERSION_PROPERTY"   , ::CabalVersionField       )
        val URL_FIELD          : IElementType = CabalCompositeElementType("URL_FIELD"                , ::URLField                )
        val NAME_FIELD         : IElementType = CabalCompositeElementType("NAME_FIELD"               , ::NameField               )
        val FILE_LIST          : IElementType = CabalCompositeElementType("FILE_LIST"                , ::FileListField           )
        val FREE_FIELD         : IElementType = CabalCompositeElementType("FREE_FORM_FIELD"          , ::FreeField               )
        val BUILDABLE          : IElementType = CabalCompositeElementType("BUILDABLE"                , ::BuildableField          )
        val EXTENSIONS         : IElementType = CabalCompositeElementType("EXTENSIONS"               , ::ExtensionsField         )
        val OTHER_MODULES      : IElementType = CabalCompositeElementType("OTHER_MODULES"            , ::OtherModulesField       )
        val DIRECTORY_FIELD    : IElementType = CabalCompositeElementType("DIRECTORY_FIELD"          , ::DirectoryField          )
        val BUILD_TYPE         : IElementType = CabalCompositeElementType("BUILD_TYPE"               , ::BuildTypeField          )
        val LICENSE            : IElementType = CabalCompositeElementType("LICENSE"                  , ::LicenseField            )
        val TESTED_WITH        : IElementType = CabalCompositeElementType("TESTED_WITH"              , ::TestedWithField         )
        val LICENSE_FILES      : IElementType = CabalCompositeElementType("LICENSE_FILES"            , ::LicenseFilesField       )
        val DIRECTORY_LIST     : IElementType = CabalCompositeElementType("DIRECTORY_LIST"           , ::DirectoryListField      )
        val BOOL_FIELD         : IElementType = CabalCompositeElementType("BOOL_FIELD"               , ::BoolField               )
        val TOKEN_FIELD        : IElementType = CabalCompositeElementType("TOKEN_FIELD"              , ::TokenField              )
        val TOKEN_LIST         : IElementType = CabalCompositeElementType("TOKEN_LIST"               , ::TokenListField          )

        val MAIN_FILE          : IElementType = CabalCompositeElementType("MAIN_FILE"                , ::MainFileField           )

        val EXPOSED_MODULES    : IElementType = CabalCompositeElementType("EXPOSED_MODULES"          , ::ExposedModulesField     )
        val EXPOSED            : IElementType = CabalCompositeElementType("EXPOSED"                  , ::ExposedField            )

        val BUILD_DEPENDS      : IElementType = CabalCompositeElementType("BUILD_DEPENDS"            , ::BuildDependsField       )
        val PKG_CONFIG_DEPENDS : IElementType = CabalCompositeElementType("PKG_CONFIG_DEPENDS"       , ::PkgConfigDependsField   )
        val BUILD_TOOLS        : IElementType = CabalCompositeElementType("BUILD_TOOLS"              , ::BuildToolsField         )
        val OPTIONS_FIELD      : IElementType = CabalCompositeElementType("OPTIONS_FIELD"            , ::OptionsField            )

        val VERSION_CONSTRAINT : IElementType = CabalCompositeElementType("VERSION_CONSTRAINT"       , ::VersionConstraint       )
        val FULL_CONSTRAINT    : IElementType = CabalCompositeElementType("FULL_CONSTRAINT"          , ::FullVersionConstraint   )
        val URL                : IElementType = CabalCompositeElementType("URL"                      , ::URL                     )
        val NAME               : IElementType = CabalCompositeElementType("NAME"                     , ::Name                    )
        val FILE_NAME          : IElementType = CabalCompositeElementType("FILE_NAME"                , ::FileName                )
        val FILE_REF           : IElementType = CabalCompositeElementType("FILE_REF"                 , ::FileReference           )
        val FREE_FORM          : IElementType = CabalCompositeElementType("FREE_FORM"                , ::FreeForm                )
        val CONDITION          : IElementType = CabalCompositeElementType("CONDITION"                , ::Condition               )
    }
}
