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

/**
 * Created by atsky on 12/05/14.
 */
class BuildWrapper(val path : String,
                   val cabalFile : String) {
    class object {
        public fun init(moduleContent : VirtualFile) : BuildWrapper {
            val cabals = moduleContent.getChildren()!!.filter { it.getName().endsWith(".cabal") }
            val cabal = cabals.head!!.getPath()

            return BuildWrapper(moduleContent.getPath(), cabal)
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

    fun thingatpoint(file : String, line : Int, column : Int): JSONArray? {
        val out = ProcessRunner(path).execute(
                getProgramPath(), "thingatpoint",
                "-t", ".buildwrapper",
                "--cabalfile=" + cabalFile,
                "-f", file,
                "--line", line.toString(),
                "--column", column.toString()
        )
        System.out.println(out);
        /*
        val prefix = "\nbuild-wrapper-json:"
        if (out.startsWith(prefix)) {
            val jsonText = out.substring(prefix.size)
            val array = JSONValue.parse(jsonText) as JSONArray

            return array[0] as JSONArray?
        }
        */
        return null;
    }


    fun namesinscope(file : String): JSONArray? {
        val out = ProcessRunner(path).execute(
                getProgramPath(), "namesinscope", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", file)
        val prefix = "\nbuild-wrapper-json:"
        if (out.startsWith(prefix)) {
            val jsonText = out.substring(prefix.size)
            val array = JSONValue.parse(jsonText) as JSONArray

            return array[0] as JSONArray?
        }
        return null;
    }

    fun synchronize() {
        val out = ProcessRunner(path).execute(
                getProgramPath(), "synchronize", "-t", ".buildwrapper", "--cabalfile=" + cabalFile)
    }

    fun build1(file : String) : JSONArray? {
        val out = ProcessRunner(path).execute(
                getProgramPath(), "build1", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", file)
        val prefix = "build-wrapper-json:"
        if (out.contains(prefix)) {
            val jsonText = out.substring(out.indexOf(prefix) + prefix.size)
            val array = JSONValue.parse(jsonText) as JSONArray

            return array
        }
        return null
    }
}
