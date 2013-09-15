package org.jetbrains.cabal.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.cabal.CabalFile;
import org.jetbrains.cabal.CabalLanguage;
import org.jetbrains.haskell.cabal.CabalParser;
import org.jetbrains.haskell.parser.CabalCompositeElementType;

public class CaballParserDefinition implements ParserDefinition {
    IFileElementType CABAL_FILE = new IFileElementType(CabalLanguage.INSTANCE);


    @NotNull
    public Lexer createLexer(Project project) {
        return new CabalLexer();
    }

    public IFileElementType getFileNodeType() {
        return CABAL_FILE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return CabalTokelTypes.WHITESPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return CabalTokelTypes.COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.create(CabalTokelTypes.STRING);
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new PsiParser() {

            @NotNull
            @Override
            public ASTNode parse(IElementType root, PsiBuilder builder) {
                return new CabalParser(root, builder).parse();
            }
        };
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CabalFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        if (node.getElementType() instanceof CabalCompositeElementType) {
            return ((CabalCompositeElementType) node.getElementType()).getContructor().invoke(node);
        } else {
            return new ASTWrapperPsiElement(node);
        }
    }
}
