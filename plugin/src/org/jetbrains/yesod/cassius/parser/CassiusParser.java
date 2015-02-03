package org.jetbrains.yesod.cassius.parser;

/**
 * @author Leyla H
 */

import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;


public class CassiusParser implements PsiParser {
    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder psiBuilder) {
        Marker rootmMarker = psiBuilder.mark();
        parseText(psiBuilder);
        rootmMarker.done(root);
        return psiBuilder.getTreeBuilt();
    }

    public void parseText(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
        }
    }
}
