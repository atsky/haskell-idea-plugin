package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Forall extends ASTWrapperPsiElement{
    public Forall(ASTNode node) {
        super(node);
    }
}