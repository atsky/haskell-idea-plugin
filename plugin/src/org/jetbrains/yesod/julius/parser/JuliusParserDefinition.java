package org.jetbrains.yesod.julius.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.julius.JuliusFile;
import org.jetbrains.yesod.julius.JuliusLanguage;
import org.jetbrains.yesod.julius.psi.*;

/**
 * @author Leyla H
 */

public class JuliusParserDefinition implements ParserDefinition {

    public static IFileElementType JULIUS_FILE  = new IFileElementType(JuliusLanguage.INSTANCE);
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new JuliusLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new JuliusParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return JULIUS_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return JuliusTokenTypes.WHITESPACES;
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

        if(astNode.getElementType() == JuliusTokenTypes.OPERATOR) {
            return new Operator(astNode);
        }
        if(astNode.getElementType() == JuliusTokenTypes.COMMENT) {
            return new Comments(astNode);
        }
        if(astNode.getElementType() == JuliusTokenTypes.BACKSLASH) {
            return new Backslash(astNode);
        }
        if(astNode.getElementType() == JuliusTokenTypes.CURLY) {
            return new Curly(astNode);
        }
        if(astNode.getElementType() == JuliusTokenTypes.UNDERLINE) {
            return new Underline(astNode);
        }
        if(astNode.getElementType() == JuliusTokenTypes.SIGN) {
            return new Sign(astNode);
        }
        if(astNode.getElementType() == JuliusTokenTypes.STRING) {
            return new org.jetbrains.yesod.julius.psi.String(astNode);
        }
        return new ASTWrapperPsiElement(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new JuliusFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2) {
        return ParserDefinition.SpaceRequirements.MAY;
    }
}
