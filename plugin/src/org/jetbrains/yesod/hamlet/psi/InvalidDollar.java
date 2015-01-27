package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class InvalidDollar extends ASTWrapperPsiElement{
    public InvalidDollar(ASTNode node) {
        super(node);
    }
}