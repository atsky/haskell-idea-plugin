package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.haskell.fileType.HaskellFileType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.util.Processor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace

/**
 * Class represents haskell specific breakpoint type
 *
 * @author Habibullin Marat
 */
public class HaskellLineBreakpointType():
        XLineBreakpointTypeBase (HaskellLineBreakpointType.ID, HaskellLineBreakpointType.TITLE, HaskellDebuggerEditorsProvider())
{
    class object {
        public val ID: String = "haskell-line"
        private val TITLE: String = "haskell-line-breakpoint"
    }
    /**
     * Checks if specified line with number {@code lineNumber} can be used for setting breakpoint on it
     */
    override fun canPutAt(file: VirtualFile, lineNumber: Int, project: Project): Boolean {
        println("canPutAt function entered")
        var canStopAtLine: Boolean = false
        if(file.getFileType() == HaskellFileType.INSTANCE) {
            val currentDoc: Document? = FileDocumentManager.getInstance()?.getDocument(file)
            if(currentDoc != null) {
                XDebuggerUtil.getInstance()?.iterateLine(project, currentDoc, lineNumber, object : Processor<PsiElement> {
                    override fun process(psiElement: PsiElement?): Boolean {
                        if (psiElement is PsiWhiteSpace || psiElement is PsiComment) {
                            return true
                        }
                        canStopAtLine = true
                        return false
                    }
                })
            }
        }
        return canStopAtLine
    }
}