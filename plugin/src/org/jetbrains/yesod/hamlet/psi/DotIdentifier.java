package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class DotIdentifier extends ASTWrapperPsiElement{
    public DotIdentifier (ASTNode node) {
        super(node);
    }
}
