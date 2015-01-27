package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ControlMaybe extends ASTWrapperPsiElement{
    public ControlMaybe(ASTNode node) {
        super(node);
    }
}