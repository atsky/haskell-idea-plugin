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

public class HaskellCompiler(val project: Project) : TranslatingCompiler {

    override fun isCompilableFile(file: VirtualFile?, context: CompileContext?): Boolean {
        val fileType = FileTypeManager.getInstance()!!.getFileTypeByFile(file!!)
        return HaskellFileType.INSTANCE.equals(fileType)
    }


    override fun compile(context: CompileContext?,
                         moduleChunk: Chunk<Module>?,
                         files: Array<out VirtualFile>?,
                         sink: OutputSink?) {

        val mapModulesToVirtualFiles: Map<Module, List<VirtualFile>>
        if (moduleChunk!!.getNodes()!!.size() == 1) {
            mapModulesToVirtualFiles = mapOf<Module, List<VirtualFile>>(
                        moduleChunk.getNodes()!!.iterator().next() to
                        ArrayList(files!!.toList()))
        } else {
            mapModulesToVirtualFiles = CompilerUtil.buildModuleToFilesMap(context!!, files!!)!!
        }
        for (module in moduleChunk.getNodes()!!) {
            val moduleFiles = mapModulesToVirtualFiles.get(module)
            if (moduleFiles == null) {
                continue
            }

            val index = ModuleRootManager.getInstance(module)!!.getFileIndex()
            val toCompile = ArrayList<VirtualFile>()
            val toCompileTests = ArrayList<VirtualFile>()
            val configuration = CompilerConfiguration.getInstance(project)!!

            if (isAcceptableModuleType(module)) {
                for (file in moduleFiles) {
                    if (shouldCompile(file, configuration)) {
                        ((if (index.isInTestSourceContent(file))
                            toCompileTests
                        else
                            toCompile)).add(file)
                    }
                }
            }

            if (!toCompile.isEmpty()) {
                compileFiles(context!!, module, toCompile, sink!!, false)
            }
            if (!toCompileTests.isEmpty()) {
                compileFiles(context!!, module, toCompileTests, sink!!, true)
            }
        }
    }

    override fun getDescription(): String {
        return "Haskell compiler"
    }

    override fun validateConfiguration(compileScope: CompileScope?): Boolean {
        return true
    }

    class object {

        fun getMainOutput(compileContext: CompileContext, module: Module, tests: Boolean): VirtualFile? =
            if (tests)
                compileContext.getModuleOutputDirectoryForTests(module)
            else
                compileContext.getModuleOutputDirectory(module)


        private fun compileFiles(context: CompileContext, module: Module, toCompile: MutableList<VirtualFile>, sink: OutputSink, tests: Boolean) {

            val outputDir = getMainOutput(context, module, tests)!!
            val output = ArrayList<OutputItem>()

            sink.add(outputDir.getPath(), output, VfsUtilCore.toVirtualFileArray(toCompile))

            for (file in toCompile) {
                for (message in GHCInterface().runGHC(module, file, outputDir)) {
                    context.addMessage(CompilerMessageCategory.ERROR, message.myText, "file://" + message.myFile, message.myLine, message.myCol)
                }
            }
        }

        private fun shouldCompile(file: VirtualFile, configuration: CompilerConfiguration): Boolean {
            return !configuration.isResourceFile(file)
        }

        private fun isAcceptableModuleType(module: Module): Boolean {
            return HaskellModuleType.get(module) is HaskellModuleType
        }
    }
}
