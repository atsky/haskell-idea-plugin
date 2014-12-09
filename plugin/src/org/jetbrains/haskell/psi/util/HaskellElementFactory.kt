package org.jetbrains.haskell.psi.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.haskell.fileType.HaskellFile


public object HaskellElementFactory {

    public fun createExpressionFromText(project: Project, name: String): PsiElement {
        val fileFromText = createFileFromText(project, name)
        val expression = fileFromText.getFirstChild().getFirstChild().getFirstChild()
        return expression.getFirstChild()
    }

    public fun createFileFromText(project: Project, text: String): HaskellFile {
        return PsiFileFactory.getInstance(project).createFileFromText("tmp.hs", HaskellLanguage.INSTANCE, text) as HaskellFile
    }

}
