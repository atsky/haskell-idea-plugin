package org.jetbrains.haskell.external

import org.jetbrains.haskell.util.OS
import java.io.File
import org.jetbrains.haskell.util.joinPath
import org.jetbrains.haskell.util.ProcessRunner
import org.json.simple.JSONValue
import org.json.simple.JSONArray
import org.jetbrains.haskell.config.HaskellSettings
import java.io.IOException
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.util.LineColPosition
import org.json.simple.JSONObject
import org.jetbrains.haskell.util.getRelativePath
import org.jetbrains.cabal.CabalInterface
import com.intellij.notification.Notifications.Bus
import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationType

/**
 * Created by atsky on 12/05/14.
 */
class BuildWrapper(val moduleRoot: String,
                   val cabalFile : String) {
    class object {

        public fun init(element : PsiElement) : BuildWrapper {
            val moduleRoot = BuildWrapper.getModuleContentDir(element)!!
            val virtualFile = CabalInterface.findCabal(element)!!

            return BuildWrapper(moduleRoot.getPath(), virtualFile.getPath())
        }

        fun getProgramPath(): String {
            return HaskellSettings.getInstance().getState().buildWrapperPath!!
        }

        public fun check() : Boolean {
            try {
                ProcessRunner(null).executeOrFail(getProgramPath(), "-V")
                return true
            } catch(e : IOException) {
                return false
            }

        }

        fun getModuleContentDir(file: PsiElement): VirtualFile? {
            val module = ModuleUtilCore.findModuleForPsiElement(file)
            return module?.getModuleFile()?.getParent()
        }
    }

    fun thingatpoint(file : VirtualFile, pos : LineColPosition): JSONObject? {

        val relativePath = getRelativePath(moduleRoot, file.getPath())

        try {
            val out = ProcessRunner(moduleRoot).executeOrFail(
                    getProgramPath(), "thingatpoint",
                    "-t", ".buildwrapper",
                    "--cabalfile=" + cabalFile,
                    "-f", relativePath,
                    "--line", pos.myLine.toString(),
                    "--column", pos.myColumn.toString())

            val array = extractJsonArray(out)
            return if (array != null) array[0] as JSONObject? else null
        } catch (e : IOException) {
            Notifications.Bus.notify(Notification("BuildWrapper.Error", "BuildWrapper error", e.getMessage()!!, NotificationType.ERROR))
            return null
        }
    }


    fun extractJsonArray(text : String) : JSONArray? {
        val prefix = "build-wrapper-json:"
        if (text.contains(prefix)) {
            val jsonText = text.substring(text.indexOf(text) + prefix.size + 1)
            return JSONValue.parse(jsonText) as? JSONArray
        }

        return null
    }

    fun namesinscope(file : String): JSONArray? {
        val out = ProcessRunner(moduleRoot).executeNoFail(
                getProgramPath(), "namesinscope", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", file)

        val array = extractJsonArray(out)
        return if (array != null) array[0] as JSONArray? else null
    }

    fun synchronize() {
        ProcessRunner(moduleRoot).executeNoFail(
                getProgramPath(), "synchronize", "-t", ".buildwrapper", "--cabalfile=" + cabalFile)
    }

    fun build1(file : VirtualFile) : JSONArray? {
        val relativePath = getRelativePath(moduleRoot, file.getPath())

        val out = ProcessRunner(moduleRoot).executeNoFail(
                getProgramPath(), "build1", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", relativePath)

        val errorPrefix = "cabal: At least the following dependencies are missing:"
        if (out.contains(errorPrefix)) {
            val errorText = out.substring(out.indexOf(errorPrefix) + errorPrefix.size + 1)
            errorText.substring(0, errorText.indexOf("\n\n"))
            Notifications.Bus.notify(Notification("Cabal.Error",
                                                  "Packages missing",
                                                  errorText.substring(0, errorText.indexOf("\n\n")),
                                                  NotificationType.WARNING))
        }

        return extractJsonArray(out)
    }

    fun dependencies() : JSONArray? {
        val out = ProcessRunner(moduleRoot).executeNoFail(
                getProgramPath(), "dependencies", "-t", ".buildwrapper", "--cabalfile=" + cabalFile)

        return extractJsonArray(out);
    }
}
