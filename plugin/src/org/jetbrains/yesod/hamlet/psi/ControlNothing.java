package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ControlNothing extends ASTWrapperPsiElement{
    public ControlNothing(ASTNode node) {
        super(node);
    }
}