package org.jetbrains.yesod.julius.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Dollar extends ASTWrapperPsiElement {
    public Dollar(ASTNode node) {
        super(node);
    }
}