package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ElseCondition extends ASTWrapperPsiElement{
    public ElseCondition(ASTNode node) {
        super(node);
    }
}