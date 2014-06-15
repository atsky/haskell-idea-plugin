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

/**
 * Created by atsky on 5/30/14.
 */
public class ShowSymbolInfoAction : AnAction() {

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
        val element = psiFile.findElementAt(offset)

        if (element == null) {
            return
        }

        val lineColPosition = LineColPosition.fromOffset(psiFile, element.getTextOffset())!!

        val info = BuildWrapper.init(element).thingatpoint(psiFile.getVirtualFile()!!, lineColPosition)

        val name = info?.get("Name") as String?
        val aType = info?.get("QType") as String?
        if (name != null && aType != null) {
            val text = name + " :: " + aType
            HintManager.getInstance()!!.showInformationHint(editor, text)
        }
    }


}