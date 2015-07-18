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


public class HamletParserDefinition : ParserDefinition {
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
        if (astNode.getElementType() === HamletTokenTypes.TAG) {
            return Tag(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.DOCTYPE) {
            return Doctype(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.STRING) {
            return org.jetbrains.yesod.hamlet.psi.String(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.DOT_IDENTIFIER) {
            return DotIdentifier(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.COLON_IDENTIFIER) {
            return ColonIdentifier(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.INTERPOLATION) {
            return Interpolation(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.SHARP_IDENTIFIER) {
            return SharpIdentifier(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.OPERATOR) {
            return Operator(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.COMMENT) {
            return Comment(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.ESCAPE) {
            return Escape(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.END_INTERPOLATION) {
            return EndInterpolation(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.ATTRIBUTE) {
            return Attribute(astNode)
        }
        if (astNode.getElementType() === HamletTokenTypes.ATTRIBUTE_VALUE) {
            return AttributeValue(astNode)
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

        public var HAMLET_FILE: IFileElementType = IFileElementType(HamletLanguage.INSTANCE)
    }
}
