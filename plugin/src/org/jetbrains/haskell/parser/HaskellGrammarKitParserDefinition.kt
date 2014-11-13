package org.jetbrains.haskell.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.tree.IFileElementType
import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.HaskellLanguage
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.extapi.psi.ASTWrapperPsiElement
import lexer.KitHaskellLexer
import generated.GeneratedTypes
import generated.GeneratedParser
import org.jetbrains.haskell.parser.token.WHITESPACES
import org.jetbrains.haskell.parser.rules.HaskellIndentLexer
import com.intellij.psi.TokenType


public class HaskellGrammarKitParserDefinition() : ParserDefinition {
    val HASKELL_FILE = IFileElementType(HaskellLanguage.INSTANCE)

    override fun createLexer(project: Project?): Lexer = HaskellIndentLexer()

    override fun getFileNodeType(): IFileElementType = HASKELL_FILE

    override fun getWhitespaceTokens() = TokenSet.create(TokenType.WHITE_SPACE, GeneratedTypes.NEW_LINE)

    override fun getCommentTokens(): TokenSet = TokenSet.create(GeneratedTypes.BLOCK_COMMENT)

    override fun getStringLiteralElements(): TokenSet = TokenSet.create()

    override fun createParser(project: Project?): PsiParser = GeneratedParser()

    override fun createFile(viewProvider: FileViewProvider?): PsiFile =
        HaskellFile(viewProvider!!)

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) =
        ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode?): PsiElement {
        return GeneratedTypes.Factory.createElement(node);
    }

}
