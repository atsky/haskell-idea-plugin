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
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.PsiDocumentManager

class HaskellDebuggerEditorsProvider : XDebuggerEditorsProvider() {

    override fun createDocument(project: Project,
                                text: String,
                                sourcePosition: XSourcePosition?,
                                mode: EvaluationMode): Document {
        if(sourcePosition != null) {
            val hsPsiFile = PsiFileFactory.getInstance(project)!!.createFileFromText(sourcePosition.file.name,
                    HaskellFileType.INSTANCE,
                    text)
            val hsDocument = PsiDocumentManager.getInstance(project)!!.getDocument(hsPsiFile)
            if(hsDocument != null) {
                return hsDocument
            }
        }
        return EditorFactory.getInstance()!!.createDocument(text)
    }

    override fun getFileType(): FileType = HaskellFileType.INSTANCE
}