package org.jetbrains.yesod.cassius.parser;

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
import org.jetbrains.yesod.cassius.CassiusFile;
import org.jetbrains.yesod.cassius.CassiusLanguage;
import org.jetbrains.yesod.cassius.psi.*;


public class CassiusParserDefinition implements ParserDefinition {

    public static IFileElementType CASSIUS_FILE  = new IFileElementType(CassiusLanguage.INSTANCE);
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CassiusLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CassiusParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return CASSIUS_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return CassiusTokenTypes.WHITESPACES;
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
      /*  if(astNode.getElementType() == HamletTokenTypes.SIGN) {
            return new Sign(astNode);
        }*/
        return new ASTWrapperPsiElement(astNode);
    }

    @NotNull
    @Override
    /*public PsiElement createElement(ASTNode astNode) {
        if(astNode.getElementType() == CassiusTokenTypes.DOT) {
            return new Tag(astNode);
        }

        return new ASTWrapperPsiElement(astNode);
    }*/

  //  @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new CassiusFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2) {
        return ParserDefinition.SpaceRequirements.MAY;
    }
}