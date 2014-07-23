package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.openapi.fileTypes.FileType
import org.jetbrains.haskell.fileType.HaskellFileType
import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.HaskellViewProvider
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.haskell.fileType.HaskellFileViewProviderFactory
import com.intellij.psi.PsiFileFactory
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.psi.impl.source.PsiExpressionCodeFragmentImpl

public class HaskellDebuggerEditorsProvider : XDebuggerEditorsProviderBase() {

    override fun createExpressionCodeFragment(project: Project, text: String, context: PsiElement?, isPhysical: Boolean): PsiFile? {
        if (context == null) {
            return null
        }
        return PsiExpressionCodeFragmentImpl(project, isPhysical,
                context.getContainingFile()!!.getVirtualFile()!!.getCanonicalPath()!!, text, null, context)
    }

    override fun getFileType(): FileType = HaskellFileType.INSTANCE
}