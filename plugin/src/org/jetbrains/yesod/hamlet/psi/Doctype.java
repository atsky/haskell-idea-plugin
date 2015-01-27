package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Doctype extends ASTWrapperPsiElement{
    public Doctype(ASTNode node) {
        super(node);
    }
}