package org.jetbrains.haskell.util

import com.intellij.openapi.util.SystemInfo

object OSUtil {
    val newLine = System.getProperty("line.separator").toString()

    val isLinux: Boolean = SystemInfo.isLinux

    val isWindows: Boolean = SystemInfo.isWindows

    val isMac: Boolean = SystemInfo.isMac

    @JvmStatic fun getCabalData(): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, "cabal")
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Haskell")
        } else {
            joinPath(System.getProperty("user.home")!!, ".cabal")
        }
    }

    @JvmStatic fun getCabalConfig(): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, "cabal", "config")
        } else {
            joinPath(System.getProperty("user.home")!!, ".cabal", "config")
        }
    }

    @JvmStatic fun getDefaultCabalBin(): String = joinPath(getCabalData(), "bin")

    fun getProgramDataFolder(name: String): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, name)
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Application Support", name)
        } else {
            joinPath(System.getProperty("user.home")!!, "." + name)
        }
    }

    @JvmStatic
    fun getExe(cmd: String): String = if (isWindows) cmd + ".exe" else cmd

    fun userHome(): String = System.getProperty("user.home")!!

    fun removeExtension(name: String) : String{
        if (name.endsWith(".exe")) {
            return name.substring(name.length - 4)
        }
        return name
    }

}