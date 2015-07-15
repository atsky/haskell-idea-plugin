package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Attribute extends ASTWrapperPsiElement{
    public Attribute (ASTNode node) {
        super(node);
    }
}