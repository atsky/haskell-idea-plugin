package org.jetbrains.haskell.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import org.jetbrains.haskell.vfs.TarGzVirtualFileSystem

/**
 * Created by atsky on 10/05/14.
 */
open class CheckTarGz : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {
        val path = "/Users/atsky/Library/Haskell/repo-cache/hackage.haskell.org/cpphs/1.18.4/cpphs-1.18.4.tar.gz" +
            "!cpphs-1.18.4/README"
        val virtualFile = TarGzVirtualFileSystem.INSTANCE.findFileByPath(path)!!
        val project = e!!.getProject()
        val tManager = FileEditorManager.getInstance(project)!!
        tManager.openFile(virtualFile, true);
    }

}