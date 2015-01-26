package org.jetbrains.yesod.hamlet.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class ElseIfCondition extends ASTWrapperPsiElement{
    public ElseIfCondition(ASTNode node) {
        super(node);
    }
}