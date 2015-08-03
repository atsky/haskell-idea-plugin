package org.jetbrains.yesod.lucius.parser

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
import com.intellij.psi.impl.source.resolve.graphInference.InferenceVariable
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.yesod.lucius.LuciusFile
import org.jetbrains.yesod.lucius.LuciusLanguage
import org.jetbrains.yesod.lucius.psi.AtRule
import org.jetbrains.yesod.lucius.psi.CCIdentifier
import org.jetbrains.yesod.lucius.psi.ColonIdentifier


public class LuciusParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return LuciusLexer()
    }

    override fun createParser(project: Project): PsiParser {
        return LuciusParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return LUCIUS_FILE
    }

    override fun getWhitespaceTokens(): TokenSet {
        return LuciusTokenTypes.WHITESPACES
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(astNode: ASTNode): PsiElement {
        if (astNode.getElementType() === LuciusTokenTypes.DOT_IDENTIFIER) {
            return org.jetbrains.yesod.lucius.psi.DotIdentifier(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.NUMBER) {
            return org.jetbrains.yesod.lucius.psi.Number(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.FUNCTION) {
            return org.jetbrains.yesod.lucius.psi.Function(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.AT_IDENTIFIER) {
            return org.jetbrains.yesod.lucius.psi.AtRule(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.COLON_IDENTIFIER) {
            return org.jetbrains.yesod.lucius.psi.ColonIdentifier(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.CC_IDENTIFIER) {
            return org.jetbrains.yesod.lucius.psi.CCIdentifier(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.STRING) {
            return org.jetbrains.yesod.lucius.psi.String(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.INTERPOLATION) {
            return org.jetbrains.yesod.lucius.psi.Interpolation(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.COMMENT) {
            return org.jetbrains.yesod.lucius.psi.Comment(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.ATTRIBUTE) {
            return org.jetbrains.yesod.lucius.psi.Attribute(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.SHARP_IDENTIFIER) {
            return org.jetbrains.yesod.lucius.psi.SharpIdentifier(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.END_INTERPOLATION) {
            return org.jetbrains.yesod.lucius.psi.Interpolation(astNode)
        }
        if (astNode.getElementType() === LuciusTokenTypes.HYPERLINK) {
            return org.jetbrains.yesod.lucius.psi.Hyperlink(astNode)
        }
        return ASTWrapperPsiElement(astNode)
    }

    override fun createFile(fileViewProvider: FileViewProvider): PsiFile {
        return LuciusFile(fileViewProvider)
    }

    override fun spaceExistanceTypeBetweenTokens(astNode: ASTNode, astNode2: ASTNode): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    companion object {

        public var LUCIUS_FILE: IFileElementType = IFileElementType(LuciusLanguage.INSTANCE)
    }
}