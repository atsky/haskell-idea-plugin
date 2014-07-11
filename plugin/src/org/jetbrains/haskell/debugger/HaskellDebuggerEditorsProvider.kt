package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.openapi.fileTypes.FileType
import org.jetbrains.haskell.fileType.HaskellFileType
import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.fileType.HaskellFileViewProviderFactory

/**
 * Created by vlad on 7/11/14.
 */

public class HaskellDebuggerEditorsProvider : XDebuggerEditorsProviderBase() {

    override fun createExpressionCodeFragment(project: Project, text: String, context: PsiElement?, isPhysical: Boolean): PsiFile? {
        if (context == null) {
            return null;
        }
//        val provider = HaskellFileViewProviderFactory().createFileViewProvider(null, context.getLanguage(), null, false)
//        return if (provider == null) null else HaskellFile(provider)
        throw UnsupportedOperationException()
    }
    override fun getFileType(): FileType =
            HaskellFileType.INSTANCE
}