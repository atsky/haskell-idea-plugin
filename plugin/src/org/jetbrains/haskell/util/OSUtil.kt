package org.jetbrains.haskell.util

import com.intellij.openapi.util.SystemInfo

public object OSUtil {
    val newLine = System.getProperty("line.separator").toString();

    public val isLinux: Boolean = SystemInfo.isLinux

    public val isWindows: Boolean = SystemInfo.isWindows

    public val isMac: Boolean = SystemInfo.isMac

    @JvmStatic
    public fun getCabalData(): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, "cabal")
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Haskell")
        } else {
            joinPath(System.getProperty("user.home")!!, ".cabal")
        }
    }

    @JvmStatic
    public fun getCabalConfig(): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, "cabal", "config")
        } else {
            joinPath(System.getProperty("user.home")!!, ".cabal", "config")
        }
    }

    @JvmStatic
    public fun getDefaultCabalBin(): String = joinPath(getCabalData(), "bin")

    fun getProgramDataFolder(name: String): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, name);
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Application Support", name);
        } else {
            joinPath(System.getProperty("user.home")!!, "." + name);
        }
    }

    @JvmStatic
    fun getExe(cmd: String): String = if (isWindows) cmd + ".exe" else cmd

    fun userHome(): String = System.getProperty("user.home")!!

}