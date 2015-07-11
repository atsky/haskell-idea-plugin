package org.jetbrains.yesod.julius.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Curly extends ASTWrapperPsiElement {
    public Curly(ASTNode node) {
        super(node);
    }
}