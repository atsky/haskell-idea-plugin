package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Tag extends ASTWrapperPsiElement{
    public Tag(ASTNode node) {
        super(node);
    }
}
