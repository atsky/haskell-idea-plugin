package org.jetbrains.haskell.util

import java.io.File

/**
 * Created by Евгений on 04.01.14.
 */
public val OS: OsUtil = OsUtil()

public class OsUtil() {
    val osName = System.getProperty("os.name")!!.toLowerCase();

    public val isWindows: Boolean = (osName.indexOf("win") >= 0)

    public val isMac: Boolean = (osName.indexOf("mac") >= 0) || (osName.indexOf("darwin") >= 0);

    public fun getCabalBin(): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, "cabal", "bin")
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Haskell", "bin")
        } else {
            joinPath(System.getProperty("user.home")!!, ".cabal", "bin")
        }
    }

    fun getProgramDataFolder(name: String): String {
        return if (isWindows) {
            joinPath(System.getenv("AppData")!!, name);
        } else if (isMac) {
            joinPath(System.getProperty("user.home")!!, "Library", "Application Support", name);
        } else {
            joinPath(System.getProperty("user.home")!!, "." + name);
        }
    }

}