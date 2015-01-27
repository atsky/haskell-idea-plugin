package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ControlCase extends ASTWrapperPsiElement{
    public ControlCase(ASTNode node) {
        super(node);
    }
}