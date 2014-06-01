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

/**
 * Created by atsky on 12/05/14.
 */
class BuildWrapper(val moduleRoot: String,
                   val cabalFile : String) {
    class object {

        public fun init(element : PsiElement) : BuildWrapper {
            val moduleRoot = BuildWrapper.getModuleContentDir(element)
            val virtualFile = CabalInterface.findCabal(element.getContainingFile()!!)!!

            return BuildWrapper(moduleRoot.getPath(), virtualFile.getPath())
        }

        fun getProgramPath(): String {
            return HaskellSettings.getInstance().getState().buildWrapperPath!!
        }

        public fun check() : Boolean {
            try {
                ProcessRunner(null).execute(listOf(getProgramPath(), "-V"))
                return true
            } catch(e : IOException) {
                return false
            }

        }

        fun getModuleContentDir(file: PsiElement): VirtualFile {
            val module = ModuleUtilCore.findModuleForPsiElement(file)
            return module!!.getModuleFile()!!.getParent()!!
        }
    }

    fun thingatpoint(file : VirtualFile, pos : LineColPosition): JSONObject? {

        val relativePath = getRelativePath(moduleRoot, file.getPath())

        val out = ProcessRunner(moduleRoot).execute(
                getProgramPath(), "thingatpoint",
                "-t", ".buildwrapper",
                "--cabalfile=" + cabalFile,
                "-f", relativePath,
                "--line", pos.myLine.toString(),
                "--column", pos.myColumn.toString()
        )

        val array = extractJsonArray(out)
        return if (array != null) array[0] as JSONObject? else null
    }


    fun extractJsonArray(text : String) : JSONArray? {
        val prefix = "\nbuild-wrapper-json:"
        if (text.startsWith(prefix)) {
            val jsonText = text.substring(prefix.size)
            return JSONValue.parse(jsonText) as JSONArray


        }

        return null
    }

    fun namesinscope(file : String): JSONArray? {
        val out = ProcessRunner(moduleRoot).execute(
                getProgramPath(), "namesinscope", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", file)

        val array = extractJsonArray(out)
        return if (array != null) array[0] as JSONArray? else null
    }

    fun synchronize() {
        ProcessRunner(moduleRoot).execute(
                getProgramPath(), "synchronize", "-t", ".buildwrapper", "--cabalfile=" + cabalFile)
    }

    fun build1(file : VirtualFile) : JSONArray? {
        val relativePath = getRelativePath(moduleRoot, file.getPath())

        val out = ProcessRunner(moduleRoot).execute(
                getProgramPath(), "build1", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", relativePath)

        return extractJsonArray(out)
    }

    fun dependencies() : JSONArray? {
        val out = ProcessRunner(moduleRoot).execute(
                getProgramPath(), "dependencies", "-t", ".buildwrapper", "--cabalfile=" + cabalFile)

        return extractJsonArray(out);
    }
}
