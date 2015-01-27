package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class IfCondition extends ASTWrapperPsiElement{
    public IfCondition(ASTNode node) {
        super(node);
    }
}