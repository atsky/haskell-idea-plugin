package org.jetbrains.haskell.external

import com.intellij.openapi.project.Project
import com.intellij.openapi.components.ProjectComponent
import org.jetbrains.haskell.config.HaskellSettings
import java.io.InputStream
import java.io.OutputStream
import java.io.Writer
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import com.intellij.openapi.module.ModuleManager
import java.io.File
import java.util.regex.Pattern
import java.util.ArrayList
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.ProgressIndicator

/**
 * Created by atsky on 15/06/14.
 */
public class GhcModi(val project: Project, val settings: HaskellSettings) : ProjectComponent {
    var process: Process? = null;

    override fun projectOpened() {
        val builder = ProcessBuilder(getPath())
        builder.directory(File(project.getBaseDir()!!.getPath()))
        process = builder.start()
    }

    fun getPath(): String {
        return settings.getState().ghcModiPath!!
    }

    override fun projectClosed() {
        val process = process
        if (process != null) {
            ProgressManager.getInstance()!!.runProcessWithProgressSynchronously({
                synchronized(process) {
                    val output = OutputStreamWriter(process.getOutputStream()!!)
                    output.write("\n")
                    output.flush()
                    process.waitFor()
                }
            }, "stopping ghc-modi", false, project)

            this.process = null
        }
    }

    override fun initComponent() {

    }

    override fun disposeComponent() {

    }

    override fun getComponentName(): String = "ghc-modi"


    fun runCommand(command: String): List<String> {
        val process = process
        if (process == null) {
            return listOf()
        }
        return synchronized(process) {
            val input = InputStreamReader(process.getInputStream()!!)
            val output = OutputStreamWriter(process.getOutputStream()!!)
            output.write(command + "\n")
            output.flush()

            val lines = ArrayList<String>()

            while (lines.size < 2 ||
            (!lines[lines.size - 2].startsWith("OK") &&
             !lines[lines.size - 2].startsWith("NG"))) {
                val char = CharArray(16 * 1024)
                val size = input.read(char)
                val result = java.lang.String(char, 0, size)
                val split = result.split("\n", -1)
                if (lines.isEmpty()) {
                    lines.add(split[0])
                } else {
                    val last = lines.size - 1
                    lines[last] = lines[last] + split[0]
                }
                lines.addAll(split.toList().subList(1, split.size))
            }

            lines
        }
    }

}