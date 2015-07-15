package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Comment extends ASTWrapperPsiElement{
    public Comment(ASTNode node) {
        super(node);
    }
}