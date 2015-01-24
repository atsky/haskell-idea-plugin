package org.jetbrains.yesod.hamlet.parser;

import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;


public class HamletParser implements PsiParser {
    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder psiBuilder) {
        Marker rootmMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            Marker marker = psiBuilder.mark();
            psiBuilder.advanceLexer();
            marker.done(HamletTokenTypes.ANY);
        }
        rootmMarker.done(root);
        return psiBuilder.getTreeBuilt();
    }

}
