package org.jetbrains.cabal.references

import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.Path
import com.intellij.openapi.util.TextRange

class FilePsiReference<T: Path>(element: T, resolveTo: PsiElement): PsiReferenceBase.Immediate<T>(element, element.getDefaultTextRange(), resolveTo)
