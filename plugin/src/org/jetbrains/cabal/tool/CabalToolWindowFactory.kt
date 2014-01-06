package org.jetbrains.cabal.tool


import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.jetbrains.cabal.CabalInterface
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.util.ArrayList
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vcs.changes.RunnableBackgroundableWrapper


public class CabalToolWindowFactory() : ToolWindowFactory {
    private var toolWindow: ToolWindow? = null
    private var packages : JBList? = null
    private var project: Project? = null

    override fun createToolWindowContent(project: Project?, toolWindow: ToolWindow?) {
        this.project = project!!
        this.toolWindow = toolWindow!!
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory!!.createContent(createToolWindowPanel(), "", false)
        toolWindow.getContentManager()!!.addContent(content)
    }

    private fun createToolWindowPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(getToolbar(), BorderLayout.PAGE_START)
        val list = ArrayList<String>()
        val packagesList = CabalInterface(project!!).getPackagesList()
        for (pkg in packagesList) {
            list.add(pkg.name)
        }
        packages = JBList(list)
        panel.add(JScrollPane(packages), BorderLayout.CENTER)
        return panel
    }

    private fun getToolbar(): JComponent {
        val panel = JPanel()
        panel.setLayout(BoxLayout(panel, BoxLayout.X_AXIS))
        panel.add(JButton(object : AbstractAction("Update") {
            override fun actionPerformed(e: ActionEvent) {
                CabalInterface(project!!).update();
            }
        }))

        panel.add(JButton(object : AbstractAction("Install") {
            override fun actionPerformed(e: ActionEvent) {
                val value = packages?.getSelectedValue()
                if (value != null) {
                    CabalInterface(project!!).install(value as String)
                    ProgressManager.getInstance()!!.run(RunnableBackgroundableWrapper(
                            project, "cabal install", {

                    }
                    ))
                }
            }
        }))

        return panel
    }


}