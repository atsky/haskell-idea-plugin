package org.jetbrains.yesod.julius.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class String extends ASTWrapperPsiElement {
    public String(ASTNode node) {
        super(node);
    }
}