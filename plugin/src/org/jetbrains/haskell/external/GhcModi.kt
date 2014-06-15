package org.jetbrains.haskell.external

import com.intellij.openapi.project.Project
import com.intellij.openapi.components.ProjectComponent
import org.jetbrains.jps.cabal.ProcessWrapper
import org.jetbrains.haskell.config.HaskellSettings
import java.io.InputStream
import java.io.OutputStream
import java.io.Writer
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import com.intellij.openapi.module.ModuleManager
import java.io.File
import java.util.regex.Pattern

/**
 * Created by atsky on 15/06/14.
 */
public class GhcModi(val project: Project, val settings : HaskellSettings) : ProjectComponent {
    var process : Process? = null;

    override fun projectOpened() {
        val module = ModuleManager.getInstance(project)!!.getModules().get(0)
        val file = module.getModuleFile()?.getParent()
        val builder = ProcessBuilder(getPath())
        builder.directory(File(file!!.getPath()))
        process = builder.start()
    }

    fun getPath() : String {
        return settings.getState().ghcModiPath!!
    }

    override fun projectClosed() {
        val process = process
        if (process != null) {
            val output = OutputStreamWriter(process.getOutputStream()!!)
            output.write("\n")
            output.flush()
            process.waitFor()
            this.process = null
        }
    }

    override fun initComponent() {

    }

    override fun disposeComponent() {

    }

    override fun getComponentName(): String ="ghc-modi"


    fun runCommand(command : String): List<String> {
        val process = process
        if (process == null) {
            return listOf()
        }
        val input = InputStreamReader(process.getInputStream()!!)
        val output = OutputStreamWriter(process.getOutputStream()!!)
        output.write(command + "\n")
        output.flush()

        val buffer = StringBuffer()

        while (!(buffer.toString().contains("\nOK") || buffer.toString().contains("\nNG"))) {
            val char = CharArray(16 * 1024)
            val size = input.read(char)
            buffer.append(char, 0, size)
        }
        val strings = buffer.toString().split("\n")

        return strings.toList()
    }

}