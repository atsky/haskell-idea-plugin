package org.jetbrains.cabal

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.MessageView
import org.jetbrains.cabal.tool.CabalMessageView
import org.jetbrains.haskell.util.ProcessRunner
import javax.swing.*
import java.io.IOException
import com.intellij.openapi.util.Key
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.psi.PsiFile
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiManager

private val KEY: Key<CabalMessageView> = Key.create("CabalMessageView.KEY")!!

public open class CabalInterface(val project: Project, val cabalFile: VirtualFile) {

    private open fun runCommand(canonicalPath: String, command: String): Process {
        val process = ProcessRunner(canonicalPath).getProcess(array("cabal", command))
        val ijMessageView = MessageView.SERVICE.getInstance(project)!!
        for (content in ijMessageView.getContentManager()!!.getContents()) {
            val cabalMessageView = content.getUserData(KEY)
            if (cabalMessageView != null) {
                ijMessageView.getContentManager()?.removeContent(content, true)
            }
        }
        val cabalMessageView = CabalMessageView(project, process)
        val content: Content = ContentFactory.SERVICE.getInstance()!!.createContent(cabalMessageView.getComponent(), "Cabal console", true)
        content.putUserData(KEY, cabalMessageView)
        ijMessageView.getContentManager()!!.addContent(content)
        ijMessageView.getContentManager()!!.setSelectedContent(content)

        val messageToolWindow = ToolWindowManager.getInstance(project)?.getToolWindow(ToolWindowId.MESSAGES_WINDOW)
        messageToolWindow?.activate(null)
        return process
    }

    public open fun configure(): Process {
        return runCommand(cabalFile.getParent()!!.getCanonicalPath()!!, "configure")
    }

    public open fun build(): Process {
        return runCommand(cabalFile.getParent()!!.getCanonicalPath()!!, "build")
    }

    public open fun clean(): Process {
        return runCommand(cabalFile.getParent()!!.getCanonicalPath()!!, "clean")
    }

    private open fun findCabal(): String? {
        for (file : VirtualFile? in project.getBaseDir()!!.getChildren()!!) {
            if ("cabal".equals(file?.getExtension())) {
                val cachedDocument: Document? = FileDocumentManager.getInstance()?.getCachedDocument(file!!)
                if (cachedDocument != null) {
                    ApplicationManager.getApplication()!!.runWriteAction(object : Runnable {
                        public override fun run(): Unit {
                            FileDocumentManager.getInstance()?.saveDocument(cachedDocument)
                        }


                    })
                }
                return file.getParent()?.getCanonicalPath()!!
            }

        }
        Notifications.Bus.notify(Notification("Cabal.Error", "Cabal error", "Can't find cabal file.", NotificationType.ERROR))
        return null
    }

    fun getPsiFile() : CabalFile {
        return PsiManager.getInstance(project).findFile(cabalFile) as CabalFile
    }

}


public fun findCabal(module: Module): CabalInterface? {
    val children = module.getModuleFile()?.getParent()?.getChildren()
    var cabalFile: VirtualFile? = null;

    if (children != null) {
        for (file in children) {
            if ("cabal".equals(file.getExtension())) {
                cabalFile = file;
                break;
            }
        }
    }
    if (cabalFile != null) {
        return CabalInterface(module.getProject(), cabalFile!!)
    }
    return null;
}

public fun findCabal(file: PsiFile, project: Project): CabalInterface? {
    val projectFileIndex = ProjectRootManager.getInstance(project)!!.getFileIndex()
    return findCabal(projectFileIndex.getModuleForFile(file.getVirtualFile()!!)!!)
}