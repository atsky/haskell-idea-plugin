package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.openapi.fileTypes.FileType
import org.jetbrains.haskell.fileType.HaskellFileType

public class HaskellDebuggerEditorsProvider : XDebuggerEditorsProviderBase() {

    override fun createExpressionCodeFragment(project: Project, text: String, context: PsiElement?, isPhysical: Boolean): PsiFile? {
        if (context == null) {
            return null;
        }
        return null;
    }

    override fun getFileType(): FileType = HaskellFileType.INSTANCE
}