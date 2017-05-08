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

class HaskellUtils {
    companion object {
        fun zeroBasedToHaskellLineNumber(zeroBasedFileLineNumber: Int) = zeroBasedFileLineNumber + 1
        fun haskellLineNumberToZeroBased(haskellFileLineNumber: Int) = haskellFileLineNumber - 1

        fun getModuleName(project: Project, file: VirtualFile): String {
            class NameReader(val project: Project, val file: VirtualFile) : Runnable {

                private var read: Boolean = false
                private var name: String? = null
                private val lock = ReentrantLock()
                private val condition = lock.newCondition()

                override fun run() {
                    lock.lock()
                    val hsFile = PsiManager.getInstance(project).findFile(file) as HaskellFile
                    name = hsFile.getModule()!!.getModuleName()!!.text!!
                    read = true
                    condition.signalAll()
                    lock.unlock()
                }

                fun returnName(): String {
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

        val HS_BOOLEAN_TYPENAME: String = "Bool"
        val HS_BOOLEAN_TRUE: String = "True"
    }
}