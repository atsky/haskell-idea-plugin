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
        val LEFT_PAREN         : IElementType  = HaskellToken("(")
        val RIGHT_PAREN        : IElementType  = HaskellToken(")")
        val DOT                : IElementType  = HaskellToken(".")
        val END_OF_LINE_COMMENT: IElementType? = HaskellToken("--")
        val STRING             : IElementType  = HaskellToken("string")
        val NUMBER             : IElementType  = HaskellToken("number")
        val ID                 : IElementType  = HaskellToken("id")
        val COMPARATOR         : IElementType  = HaskellToken("COMPARATOR")
        val LOGIC              : IElementType  = HaskellToken("LOGIC")

        val COMMENTS     : TokenSet = TokenSet.create(END_OF_LINE_COMMENT, COMMENT)
        val WHITESPACES  : TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

        val PROPERTY           : IElementType = CabalCompositeElementType("PROPERTY", defaultContructor)
        val PROPERTY_KEY       : IElementType = CabalCompositeElementType("PROPERTY_KEY", ::PropertyKey)
        val PROPERTY_VALUE     : IElementType = CabalCompositeElementType("PROPERTY_VALUE" , defaultContructor)
        val EXECUTABLE         : IElementType = CabalCompositeElementType("EXECUTABLE", ::Executable)
        val TEST_SUITE         : IElementType = CabalCompositeElementType("TEST_SUITE", ::TestSuite)
        val SECTION            : IElementType = CabalCompositeElementType("SECTION", defaultContructor)
        val SECTION_TYPE       : IElementType = CabalCompositeElementType("SECTION_TYPE", ::SectionType)


        val VERSION            : IElementType = CabalCompositeElementType("VERSION_PROPERTY"         , ::VersionProperty         )
        val CABAL_VERSION      : IElementType = CabalCompositeElementType("CABAL_VERSION_PROPERTY"   , ::CabalVersionField       )
        val HOMEPAGE           : IElementType = CabalCompositeElementType("HOMEPAGE"                 , ::HomepageField           )
        val PACKAGE_URL        : IElementType = CabalCompositeElementType("PACKAGE_URL"              , ::PackageURLField         )
        val NAME_FIELD         : IElementType = CabalCompositeElementType("NAME_FIELD"               , ::NameField               )
        val MAIN_FILE          : IElementType = CabalCompositeElementType("MAIN_FILE"                , ::MainFile                )
        val DATA_FILES         : IElementType = CabalCompositeElementType("DATA_FILES"               , ::DataFiles               )
        val EXTRA_SOURCE       : IElementType = CabalCompositeElementType("EXTRA_SOURCE"             , ::ExtraSource             )
        val EXTRA_TMP          : IElementType = CabalCompositeElementType("EXTRA_TMP"                , ::ExtraTmp                )
        val EXTRA_DOC          : IElementType = CabalCompositeElementType("EXTRA_DOC"                , ::ExtraDoc                )

        val BUILD_DEPENDS      : IElementType = CabalCompositeElementType("BUILD_DEPENDS"            , ::BuildDependsField       )

        val VERSION_CONSTRAINT : IElementType = CabalCompositeElementType("VERSION_CONSTRAINT"       , ::VersionConstraint       )
        val FULL_CONSTRAINT    : IElementType = CabalCompositeElementType("FULL_CONSTRAINT"          , ::FullVersionConstraint   )
        val URL                : IElementType = CabalCompositeElementType("URL"                      , ::URL                     )
        val NAME               : IElementType = CabalCompositeElementType("NAME"                     , ::Name                    )
        val FILE_NAME          : IElementType = CabalCompositeElementType("FILE_NAME"                , ::FileName                )
        val FILE_REF           : IElementType = CabalCompositeElementType("FILE_REF"                 , ::FileReference           )
    }
}
