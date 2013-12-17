package org.jetbrains.haskell.highlight;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.haskell.parser.token.HaskellTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HaskellBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = {
        new BracePair(HaskellTokenTypes.LEFT_PAREN, HaskellTokenTypes.RIGHT_PAREN, true),
        new BracePair(HaskellTokenTypes.LEFT_BRACE, HaskellTokenTypes.RIGHT_BRACE, true),
        new BracePair(HaskellTokenTypes.LEFT_BRACKET, HaskellTokenTypes.RIGHT_BRACKET, true)
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
