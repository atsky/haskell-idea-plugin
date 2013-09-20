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

public open class CabalInterface(project: Project) {
    private val myProject: Project = project

    public open fun configure(): Unit {
        runCommand(findCabal()!!, "configure")
    }

    private open fun runCommand(canonicalPath: String, command: String): Unit {
        val process = ProcessRunner(canonicalPath).getProcess("cabal", command)!!
        val ijMessageView = MessageView.SERVICE.getInstance(myProject)!!
        val component: JComponent? = CabalMessageView(myProject, process).getComponent()
        val content: Content = ContentFactory.SERVICE.getInstance()!!.createContent(component, "Cabal console", true)
        ijMessageView.getContentManager()!!.addContent(content)
        ijMessageView.getContentManager()!!.setSelectedContent(content)

        val messageToolWindow = ToolWindowManager.getInstance(myProject)?.getToolWindow(ToolWindowId.MESSAGES_WINDOW)
        messageToolWindow?.activate(null)
    }

    public open fun build(): Unit {
        runCommand(findCabal()!!, "build")
    }
    public open fun clean(): Unit {
        runCommand(findCabal()!!, "clean")
    }
    private open fun findCabal(): String? {
        for (file : VirtualFile? in myProject.getBaseDir()!!.getChildren()!!) {
            if ("cabal".equals(file?.getExtension())) {
                val cachedDocument: Document? = FileDocumentManager.getInstance()?.getCachedDocument(file!!)
                if (cachedDocument != null) {
                    ApplicationManager.getApplication()!!.runWriteAction(object : Runnable {
                        public override fun run(): Unit {
                            FileDocumentManager.getInstance()?.saveDocument(cachedDocument!!)
                        }


                    })
                }
                return file.getParent()?.getCanonicalPath()
            }

        }
        return null
    }

}
