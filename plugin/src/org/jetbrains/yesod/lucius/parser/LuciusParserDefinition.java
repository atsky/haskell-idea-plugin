package org.jetbrains.yesod.lucius.parser;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.graphInference.InferenceVariable;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.lucius.LuciusFile;
import org.jetbrains.yesod.lucius.LuciusLanguage;
import org.jetbrains.yesod.lucius.psi.AtRule;
import org.jetbrains.yesod.lucius.psi.CCIdentifier;
import org.jetbrains.yesod.lucius.psi.ColonIdentifier;


public class LuciusParserDefinition implements ParserDefinition {

    public static IFileElementType LUCIUS_FILE  = new IFileElementType(LuciusLanguage.INSTANCE);
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new LuciusLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new LuciusParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return LUCIUS_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return LuciusTokenTypes.WHITESPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        if(astNode.getElementType() == LuciusTokenTypes.DOT_IDENTIFIER) {
            return new org.jetbrains.yesod.lucius.psi.AtRule(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.NUMBER) {
            return new org.jetbrains.yesod.lucius.psi.Number(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.FUNCTION) {
            return new org.jetbrains.yesod.lucius.psi.Function(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.AT_IDENTIFIER) {
            return new org.jetbrains.yesod.lucius.psi.AtRule(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.COLON_IDENTIFIER) {
            return new org.jetbrains.yesod.lucius.psi.ColonIdentifier(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.CC_IDENTIFIER) {
            return new org.jetbrains.yesod.lucius.psi.CCIdentifier(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.STRING) {
            return new org.jetbrains.yesod.lucius.psi.String(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.INTERPOLATION) {
            return new org.jetbrains.yesod.lucius.psi.Interpolation(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.COMMENT) {
            return new org.jetbrains.yesod.lucius.psi.Comment(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.ATTRIBUTE) {
            return new org.jetbrains.yesod.lucius.psi.Attribute(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.SHARP_IDENTIFIER) {
            return new org.jetbrains.yesod.lucius.psi.SharpIdentifier(astNode);
        }
        if(astNode.getElementType() == LuciusTokenTypes.END_INTERPOLATION) {
            return new org.jetbrains.yesod.lucius.psi.Interpolation(astNode);
        }
        return new ASTWrapperPsiElement(astNode);
    }

    @NotNull
    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new LuciusFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2) {
        return ParserDefinition.SpaceRequirements.MAY;
    }
}