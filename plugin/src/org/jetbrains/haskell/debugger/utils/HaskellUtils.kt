package org.jetbrains.haskell.debugger.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.jetbrains.haskell.fileType.HaskellFile
import com.intellij.openapi.application.ApplicationManager
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JPanel
import javax.swing.JComponent
import org.jetbrains.haskell.util.gridBagConstraints
import java.awt.Insets
import javax.swing.JLabel
import org.jetbrains.haskell.util.setConstraints
import java.awt.GridBagConstraints
import javax.swing.Box

public class HaskellUtils {
    class object {
        fun zeroBasedToHaskellLineNumber(zeroBasedFileLineNumber: Int) = zeroBasedFileLineNumber + 1
        fun haskellLineNumberToZeroBased(haskellFileLineNumber: Int) = haskellFileLineNumber - 1

        public fun getModuleName(project: Project, file: VirtualFile): String {
            class NameReader(val project: Project, val file: VirtualFile) : Runnable {

                private var read: Boolean = false
                private var name: String? = null
                private val lock = ReentrantLock()
                private val condition = lock.newCondition()

                override fun run() {
                    lock.lock()
                    val hsFile = PsiManager.getInstance(project).findFile(file) as HaskellFile
                    val header = hsFile.getModule()!!.getNode().findChildByType(org.jetbrains.haskell.parser.grammar.MODULE_HEADER)!!
                    val moduleName = header.getChildren(null)!![2]
                    name = moduleName.getText()!!
                    read = true
                    condition.signalAll()
                    lock.unlock()
                }

                public fun returnName(): String {
                    lock.lock()
                    while (!read) {
                        condition.await()
                    }
                    lock.unlock()
                    return name!!
                }
            }

            val reader = NameReader(project, file)
            ApplicationManager.getApplication()!!.runReadAction(reader)
            return reader.returnName()
        }

        public fun addLabeledControl(panel: JPanel, row: Int, label: String, component: JComponent) {
            val base = gridBagConstraints { insets = Insets(2, 0, 2, 3) }
            panel.add(JLabel(label), base.setConstraints {
                anchor = GridBagConstraints.LINE_START
                gridx = 0;
                gridy = row;
            })
            panel.add(component, base.setConstraints {
                gridx = 1;
                gridy = row;
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            })
            panel.add(Box.createHorizontalStrut(1), base.setConstraints {
                gridx = 2;
                gridy = row;
                weightx = 0.1
            })
        }

        public val HS_BOOLEAN_TYPENAME: String = "Bool"
        public val HS_BOOLEAN_TRUE: String = "True"
    }
}