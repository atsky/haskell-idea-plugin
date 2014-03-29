package org.jetbrains.haskell.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.haskell.fileType.HaskellFile;
import org.jetbrains.haskell.HaskellLanguage;
import org.jetbrains.haskell.parser.lexer.HaskellLexer;
import org.jetbrains.haskell.parser.lexer.LexerPackage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.parser.token.TokenPackage;

public class HaskellParserDefinition implements ParserDefinition {
    IFileElementType HASKELL_FILE = new IFileElementType(HaskellLanguage.INSTANCE);


    @NotNull
    public Lexer createLexer(Project project) {
        return new HaskellLexer();
    }

    public IFileElementType getFileNodeType() {
        return HASKELL_FILE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return HaskellTokenSets.WHITESPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return HaskellTokenSets.COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.create(LexerPackage.getSTRING());
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new PsiParser() {

            @NotNull
            @Override
            public ASTNode parse(IElementType root, PsiBuilder builder) {
                return new DummyHaskellParser(root, builder).parse();
            }
        };
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new HaskellFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return TokenPackage.createElement(node);
    }
}
