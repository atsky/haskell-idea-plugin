package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ControlOf extends ASTWrapperPsiElement{
    public ControlOf(ASTNode node) {
        super(node);
    }
}