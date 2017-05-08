package org.jetbrains.yesod.julius.parser

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.yesod.hamlet.psi.*
import org.jetbrains.yesod.hamlet.psi.DotIdentifier
import org.jetbrains.yesod.hamlet.psi.Interpolation
import org.jetbrains.yesod.julius.JuliusFile
import org.jetbrains.yesod.julius.JuliusLanguage
import org.jetbrains.yesod.julius.psi.*
import org.jetbrains.yesod.julius.psi.Number

/**
 * @author Leyla H
 */

class JuliusParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return JuliusLexer()
    }

    override fun createParser(project: Project): PsiParser {
        return JuliusParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return JULIUS_FILE
    }

    override fun getWhitespaceTokens(): TokenSet {
        return JuliusTokenTypes.WHITESPACES
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(astNode: ASTNode): PsiElement {

        if (astNode.elementType === JuliusTokenTypes.KEYWORD) {
            return org.jetbrains.yesod.julius.psi.Keyword(astNode)
        }
        if (astNode.elementType === JuliusTokenTypes.COMMENT) {
            return org.jetbrains.yesod.julius.psi.Comment(astNode)
        }
        if (astNode.elementType === JuliusTokenTypes.NUMBER) {
            return org.jetbrains.yesod.julius.psi.Number(astNode)
        }
        if (astNode.elementType === JuliusTokenTypes.DOT_IDENTIFIER) {
            return org.jetbrains.yesod.julius.psi.DotIdentifier(astNode)
        }
        if (astNode.elementType === JuliusTokenTypes.INTERPOLATION) {
            return org.jetbrains.yesod.julius.psi.Interpolation(astNode)
        }
        if (astNode.elementType === JuliusTokenTypes.STRING) {
            return org.jetbrains.yesod.julius.psi.String(astNode)
        }
        if (astNode.elementType === JuliusTokenTypes.END_INTERPOLATION) {
            return org.jetbrains.yesod.julius.psi.Interpolation(astNode)
        }
        return ASTWrapperPsiElement(astNode)
    }

    override fun createFile(fileViewProvider: FileViewProvider): PsiFile {
        return JuliusFile(fileViewProvider)
    }

    override fun spaceExistanceTypeBetweenTokens(astNode: ASTNode, astNode2: ASTNode): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    companion object {

        var JULIUS_FILE: IFileElementType = IFileElementType(JuliusLanguage.INSTANCE)
    }
}
