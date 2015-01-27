package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Comments extends ASTWrapperPsiElement{
    public Comments(ASTNode node) {
        super(node);
    }
}