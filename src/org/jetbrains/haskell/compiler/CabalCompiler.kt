package org.jetbrains.haskell.compiler

import com.intellij.compiler.CompilerConfiguration
import com.intellij.compiler.impl.CompilerUtil
import com.intellij.openapi.compiler.CompileContext
import com.intellij.openapi.compiler.CompileScope
import com.intellij.openapi.compiler.CompilerMessageCategory
import com.intellij.openapi.compiler.TranslatingCompiler
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleFileIndex
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Chunk
import org.jetbrains.haskell.fileType.HaskellFileType
import org.jetbrains.haskell.module.HaskellModuleType
import java.util.*
import com.intellij.openapi.compiler.TranslatingCompiler.OutputSink
import com.intellij.openapi.compiler.TranslatingCompiler.OutputItem
import com.intellij.openapi.vfs.VfsUtilCore
import org.jetbrains.cabal.CabalInterface
import javax.swing.SwingUtilities
import com.sun.awt.AWTUtilities
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationType
import org.jetbrains.cabal.findCabal

public class CabalCompiler(val project: Project) : TranslatingCompiler {


    public override fun isCompilableFile(file: VirtualFile?, context: CompileContext?): Boolean {
        val fileType: FileType? = FileTypeManager.getInstance()?.getFileTypeByFile(file!!)
        return HaskellFileType.INSTANCE.equals(fileType)
    }


    override fun compile(context: CompileContext?, moduleChunk: Chunk<Module>?, files: Array<out VirtualFile>?, sink: TranslatingCompiler.OutputSink?) {

        for (module : Module in moduleChunk!!.getNodes()!!) {
            compile(context!!, module);
        }

    }

    public override fun getDescription(): String {
        return "Haskell cabal compiler"
    }

    public override fun validateConfiguration(compileScope: CompileScope?): Boolean {
        return true
    }

    private fun compile(context: CompileContext, module: Module): Unit {
        SwingUtilities.invokeAndWait(object : Runnable {
            override fun run() {
                val cabalInterface = findCabal(module)
                if (cabalInterface == null) {
                    Notifications.Bus.notify(Notification("Cabal.Error", "Cabal error", "Can't find cabal file.", NotificationType.ERROR))
                } else {
                    val process = cabalInterface.configure()
                    process.waitFor();
                    if (process.exitValue() == 0) {
                        cabalInterface.build().waitFor();
                    }
                }
            }
        })
    }


    private fun shouldCompile(file: VirtualFile, configuration: CompilerConfiguration): Boolean {
        return !configuration.isResourceFile(file)
    }
    private fun isAcceptableModuleType(module: Module): Boolean {
        return HaskellModuleType.get(module) is HaskellModuleType
    }
}
