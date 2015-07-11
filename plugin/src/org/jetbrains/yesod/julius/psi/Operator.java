package org.jetbrains.yesod.julius.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Operator extends ASTWrapperPsiElement {
    public Operator(ASTNode node) {
        super(node);
    }
}