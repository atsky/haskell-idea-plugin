package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ControlWith extends ASTWrapperPsiElement{
    public ControlWith(ASTNode node) {
        super(node);
    }
}