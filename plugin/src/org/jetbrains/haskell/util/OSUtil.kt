package org.jetbrains.haskell.util

import java.io.File

public object OSUtil {
    val newLine = System.getProperty("line.separator").toString();

    val osName = System.getProperty("os.name")!!.toLowerCase();

    public val isWindows: Boolean = (osName.indexOf("win") >= 0)

    public val isMac: Boolean = (osName.indexOf("mac") >= 0) || (osName.indexOf("darwin") >= 0);

    public fun getCabalData(): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, "cabal")
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Haskell")
        } else {
            joinPath(System.getProperty("user.home")!!, ".cabal")
        }
    }

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

    fun getExe(cmd : String) : String = if (isWindows) cmd + ".exe" else cmd

    fun getExe() : String = if (isWindows) ".exe" else ""
}