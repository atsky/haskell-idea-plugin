package org.jetbrains.haskell.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.codeInsight.hint.HintManager
import org.jetbrains.haskell.external.BuildWrapper
import org.jetbrains.haskell.util.getRelativePath
import org.jetbrains.haskell.util.LineColPosition
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.external.GhcModi
import java.util.regex.Pattern
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.application.ModalityState

/**
 * Created by atsky on 5/30/14.
 */
public class ShowTypeAction : AnAction() {

    private data class TypeInfo(
        val startLine: Int,
        val startCol : Int,
        val endLine: Int,
        val endCol : Int,
        val aType : String
    ) {

    }

    fun typeInfoFromString(str : String) : TypeInfo? {
        val matcher = Pattern.compile("(\\d+) (\\d+) (\\d+) (\\d+) \"(.*)\"").matcher(str)
        if (matcher.matches()) {
            return TypeInfo(
                    Integer.parseInt(matcher.group(1)!!),
                    Integer.parseInt(matcher.group(2)!!),
                    Integer.parseInt(matcher.group(3)!!),
                    Integer.parseInt(matcher.group(4)!!),
                    matcher.group(5)!!)
        }
        return null
    }

    override fun actionPerformed(e: AnActionEvent?) {
        if (e == null) {
            return
        }
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        if (editor == null || psiFile == null) {
            return
        }

        val offset = editor.getCaretModel().getOffset();
        val selectionStartOffset = editor.getSelectionModel().getSelectionStart()
        val selectionEndOffset = editor.getSelectionModel().getSelectionEnd()
        val range = if (selectionStartOffset != selectionEndOffset) {
            Pair(selectionStartOffset, selectionEndOffset)
        } else {
            val element = psiFile.findElementAt(offset)

            val textRange = element?.getTextRange()
            if (textRange == null) {
                return
            }
            Pair(textRange.getStartOffset(), textRange.getEndOffset())
        }

        ApplicationManager.getApplication()!!.invokeAndWait(object : Runnable {
            override fun run() {
                FileDocumentManager.getInstance()!!.saveAllDocuments()
            }
        }, ModalityState.any())

        val start = LineColPosition.fromOffset(psiFile, range.first)!!
        val end = LineColPosition.fromOffset(psiFile, range.second)!!

        val lineColPosition = LineColPosition.fromOffset(psiFile, range.first)!!

        val ghcModi = psiFile.getProject().getComponent(javaClass<GhcModi>())!!
        val moduleContent = BuildWrapper.getModuleContentDir(psiFile)
        val relativePath = getRelativePath(moduleContent!!.getPath(), psiFile.getVirtualFile()!!.getPath())

        val line = lineColPosition.myLine
        val column = lineColPosition.myColumn
        val cmd = "type ${relativePath} ${line} ${column}"

        val list = ghcModi.runCommand(cmd)

        val result = list.map { typeInfoFromString(it) }.filterNotNull()
        val typeInfo = result.firstOrNull {
            it.startLine == start.myLine &&
            it.startCol == start.myColumn &&
            it.endLine == end.myLine &&
            it.endCol == end.myColumn
        }
        if (typeInfo != null) {
            HintManager.getInstance()!!.showInformationHint(editor, typeInfo.aType)
        } else {
            HintManager.getInstance()!!.showInformationHint(editor, "can't calculate type")
        }
    }


}