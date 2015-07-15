package org.jetbrains.yesod.hamlet.parser;

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
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.hamlet.HamletFile;
import org.jetbrains.yesod.hamlet.HamletLanguage;
import org.jetbrains.yesod.hamlet.psi.*;


public class HamletParserDefinition implements ParserDefinition {

    public static IFileElementType HAMLET_FILE  = new IFileElementType(HamletLanguage.INSTANCE);
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new HamletLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new HamletParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return HAMLET_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return HamletTokenTypes.WHITESPACES;
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
        if(astNode.getElementType() == HamletTokenTypes.TAG) {
            return new Tag(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.DOCTYPE) {
            return new Doctype(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.STRING) {
            return new org.jetbrains.yesod.hamlet.psi.String(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.DOT_IDENTIFIER) {
            return new DotIdentifier(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.COLON_IDENTIFIER) {
            return new ColonIdentifier(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.INTERPOLATION) {
            return new Interpolation(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.SHARP_IDENTIFIER) {
            return new SharpIdentifier(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.OPERATOR) {
            return new Operator(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.COMMENT) {
            return new Comment(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.BACKSLASH) {
            return new Backslash(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.DOLLAR) {
            return new Dollar(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.END_INTERPOLATION) {
            return new EndInterpolation(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.ATTRIBUTE) {
            return new Attribute(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.ATTRIBUTE_VALUE) {
            return new AttributeValue(astNode);
        }
        if(astNode.getElementType() == HamletTokenTypes.BACKSLASH) {
            return new Backslash(astNode);
        }
        return new ASTWrapperPsiElement(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new HamletFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2) {
        return ParserDefinition.SpaceRequirements.MAY;
    }
}
