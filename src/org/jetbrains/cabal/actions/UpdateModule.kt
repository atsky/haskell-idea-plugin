package org.jetbrains.cabal.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiFile
import com.intellij.openapi.actionSystem.CommonDataKeys

/**
 * @author Evgeny.Kurbatsky
 */
public open class UpdateModule() : AnAction() {
    public override fun actionPerformed(anActionEvent: AnActionEvent?): Unit {
        val file = anActionEvent!!.getData(CommonDataKeys.PSI_FILE)!!
        val project = anActionEvent.getProject()!!
        val projectFileIndex = ProjectRootManager.getInstance(project)!!.getFileIndex()
        val module = projectFileIndex.getModuleForFile(file.getVirtualFile()!!)!!
        //module.get
        System.out.println(module.getName())
    }


}
