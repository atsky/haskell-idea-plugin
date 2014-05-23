package org.jetbrains.haskell.external

import org.jetbrains.haskell.util.OS
import java.io.File
import org.jetbrains.haskell.util.joinPath
import org.jetbrains.haskell.util.ProcessRunner
import org.json.simple.JSONValue
import org.json.simple.JSONArray
import org.jetbrains.haskell.config.HaskellSettings
import java.io.IOException

/**
 * Created by atsky on 12/05/14.
 */
class BuildWrapper(val path : String,
                   val cabalFile : String) {
    class object {
        fun getProbrammPath(): String {
            return HaskellSettings.getInstance().getState().buildWrapperPath!!
        }

        public fun check() : Boolean {
            try {
                ProcessBuilder(getProbrammPath(), "-V").start().waitFor();
                return true;
            } catch(e : IOException) {
                return false;
            }

        }
    }

    fun synchronize() {
        val out = ProcessRunner(path).execute(
                getProbrammPath(), "synchronize", "-t", ".buildwrapper", "--cabalfile=" + cabalFile)
    }

    fun build1(file : String) : JSONArray? {
        val out = ProcessRunner(path).execute(
                getProbrammPath(), "build1", "-t", ".buildwrapper", "--cabalfile=" + cabalFile, "-f", file)
        val prefix = "\nbuild-wrapper-json:"
        if (out.startsWith(prefix)) {
            val jsonText = out.substring(prefix.size)
            val array = JSONValue.parse(jsonText) as JSONArray

            return array
        }
        return null
    }
}
