package org.jetbrains.haskell.util

/**
 * Created by Евгений on 04.01.14.
 */
public val OS : OsUtil = OsUtil()

public class OsUtil() {
    val osName = System.getProperty("os.name")!!.toLowerCase();

    public val isWindows : Boolean = (osName.indexOf("win") >= 0)

}