package org.jetbrains.yesod.hamlet.parser

/**
 * @author Leyla H
 */

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
import org.jetbrains.yesod.hamlet.HamletFile
import org.jetbrains.yesod.hamlet.HamletLanguage
import org.jetbrains.yesod.hamlet.psi.*


class HamletParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return HamletLexer()
    }

    override fun createParser(project: Project): PsiParser {
        return HamletParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return HAMLET_FILE
    }

    override fun getWhitespaceTokens(): TokenSet {
        return HamletTokenTypes.WHITESPACES
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(astNode: ASTNode): PsiElement {
        if (astNode.elementType === HamletTokenTypes.TAG) {
            return org.jetbrains.yesod.hamlet.psi.Tag(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.DOCTYPE) {
            return org.jetbrains.yesod.hamlet.psi.Doctype(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.STRING) {
            return org.jetbrains.yesod.hamlet.psi.String(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.DOT_IDENTIFIER) {
            return org.jetbrains.yesod.hamlet.psi.DotIdentifier(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.COLON_IDENTIFIER) {
            return org.jetbrains.yesod.hamlet.psi.ColonIdentifier(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.INTERPOLATION) {
            return org.jetbrains.yesod.hamlet.psi.Interpolation(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.SHARP_IDENTIFIER) {
            return org.jetbrains.yesod.hamlet.psi.SharpIdentifier(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.OPERATOR) {
            return org.jetbrains.yesod.hamlet.psi.Operator(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.COMMENT) {
            return org.jetbrains.yesod.hamlet.psi.Comment(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.ESCAPE) {
            return org.jetbrains.yesod.hamlet.psi.Escape(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.END_INTERPOLATION) {
            return org.jetbrains.yesod.hamlet.psi.Interpolation(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.ATTRIBUTE) {
            return Attribute(astNode)
        }
        if (astNode.elementType === HamletTokenTypes.ATTRIBUTE_VALUE) {
            return org.jetbrains.yesod.hamlet.psi.AttributeValue(astNode)
        }
        return ASTWrapperPsiElement(astNode)
    }

    override fun createFile(fileViewProvider: FileViewProvider): PsiFile {
        return HamletFile(fileViewProvider)
    }

    override fun spaceExistanceTypeBetweenTokens(astNode: ASTNode, astNode2: ASTNode): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    companion object {

        var HAMLET_FILE: IFileElementType = IFileElementType(HamletLanguage.INSTANCE)
    }
}
