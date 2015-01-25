package org.jetbrains.yesod.hamlet.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Doctype extends ASTWrapperPsiElement{
    public Doctype(ASTNode node) {
        super(node);
    }
}