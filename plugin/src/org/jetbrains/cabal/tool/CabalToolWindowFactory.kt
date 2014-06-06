package org.jetbrains.cabal.tool


import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.jetbrains.cabal.CabalInterface
import javax.swing.*
import java.awt.event.ActionEvent
import java.util.ArrayList
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vcs.changes.RunnableBackgroundableWrapper
import com.intellij.ui.TreeUIHelper
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBScrollPane
import javax.swing.tree.TreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.DefaultMutableTreeNode
import org.jetbrains.cabal.CabalPackageShort
import java.awt.BorderLayout
import javax.swing.tree.DefaultTreeModel
import com.intellij.ui.DocumentAdapter
import javax.swing.event.DocumentEvent
import com.intellij.ui.treeStructure.Tree
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.ide.CommonActionsManager
import com.intellij.ide.actions.ContextHelpAction
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.util.IconUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.haskell.icons.HaskellIcons
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import com.intellij.openapi.progress.ProgressIndicator
import javax.swing.tree.TreeCellRenderer
import java.awt.Component
import org.jetbrains.cabal.tool.CabalToolWindowFactory.PackageData
import java.awt.Color


public class CabalToolWindowFactory() : ToolWindowFactory {
    private var toolWindow: ToolWindow? = null
    private var packages: JTree? = null
    private var project: Project? = null
    private var treeModel: DefaultTreeModel? = null

    class PackageData(val text : String, val installed : Boolean)

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

        val packagesList = CabalInterface(project!!).getPackagesList()
        val installedPackagesList = CabalInterface(project!!).getInstalledPackagesList()

        treeModel = DefaultTreeModel(getTree(packagesList, installedPackagesList, ""))
        val tree = Tree(treeModel)
        tree.setCellRenderer(object : TreeCellRenderer {
            override fun getTreeCellRendererComponent(tree: JTree,
                                                      value: Any?,
                                                      selected: Boolean,
                                                      expanded: Boolean,
                                                      leaf: Boolean,
                                                      row: Int,
                                                      hasFocus: Boolean): Component {

                val userObject = (value as DefaultMutableTreeNode).getUserObject()
                if (userObject == null) {
                    return JLabel()
                }
                val packageData = userObject as PackageData
                val label = JLabel(packageData.text)
                if (packageData.installed) {
                    label.setForeground(Color(0, 140, 0))
                }
                return label
            }

        })

        tree.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val path = tree.getPathForLocation(e.getX(), e.getY())!!;
                    val pathArray = path.getPath()

                    val packageName = pathArray[1] as DefaultMutableTreeNode
                    val packageVersion: DefaultMutableTreeNode? = if (pathArray.size == 3) {
                        (pathArray[2] as DefaultMutableTreeNode)
                    } else {
                        null
                    }

                    val menu = JPopupMenu();
                    menu.add(JMenuItem(object: AbstractAction("Install") {
                        override fun actionPerformed(e: ActionEvent) {
                            install((packageName.getUserObject() as PackageData).text,
                                    (packageVersion?.getUserObject() as PackageData?)?.text)
                        }

                    }))
                    menu.show(tree, e.getX(), e.getY());
                }
            }
        })
        tree.setRootVisible(false);
        packages = tree

        panel.add(JBScrollPane(packages), BorderLayout.CENTER)
        return panel
    }

    fun getTree(packagesList: List<CabalPackageShort>,
                installedPackagesList: List<CabalPackageShort>,
                text: String): TreeNode {
        val root = DefaultMutableTreeNode()
        for (pkg in packagesList) {
            if (text != "" && !pkg.name.capitalize().contains(text.capitalize())) {
                continue
            }

            val installed = installedPackagesList.firstOrNull { it.name == pkg.name }
            val pkgNode = DefaultMutableTreeNode(PackageData(pkg.name, installed != null))
            for (version in pkg.availableVersions) {
                val installedVersions = installed?.availableVersions ?: listOf()
                pkgNode.add(DefaultMutableTreeNode(PackageData(version, installedVersions.contains(version))))
            }
            root.add(pkgNode)

        }

        return root;
    }

    fun updateTree(text: String) {
        val packagesList = CabalInterface(project!!).getPackagesList()
        val installedPackagesList = CabalInterface(project!!).getInstalledPackagesList()
        treeModel!!.setRoot(getTree(packagesList, installedPackagesList, text))
    }

    fun install(packageName: String, packageVersion: String?) {
        val cmd = if (packageVersion == null) {
            packageName
        } else {
            packageName + "-" + packageVersion
        }
        CabalInterface(project!!).install(cmd)
    }

    private fun getToolbar(): JComponent {
        val panel = JPanel()

        panel.setLayout(BoxLayout(panel, BoxLayout.X_AXIS))


        val group = DefaultActionGroup()
        group.add(UpdateAction())

        val actionToolBar = ActionManager.getInstance()!!.createActionToolbar("CabalTool", group, true)!!

        panel.add(actionToolBar.getComponent()!!)


        val searchTextField = SearchTextField()
        searchTextField.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent?) {
                updateTree(searchTextField.getText()!!)
            }

        })


        panel.add(searchTextField)
        return panel
    }

    inner final class UpdateAction : AnAction("Update",
            "Update packages list",
            HaskellIcons.UPDATE) {


        override fun actionPerformed(e: AnActionEvent?) {
            CabalInterface(project!!).update()
        }
    }
}