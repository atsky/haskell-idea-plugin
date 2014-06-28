package org.jetbrains.haskell.external

import org.jetbrains.haskell.util.ProcessRunner
import java.io.IOException
import com.intellij.util.messages.MessageBus
import com.intellij.util.MessageBusUtil
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import org.jetbrains.haskell.util.OsUtil
import java.io.File
import org.jetbrains.haskell.util.OS
import org.jetbrains.haskell.config.HaskellSettings
import java.util.Collections
import java.util.HashMap


/**
 * Created by atsky on 3/29/14.
 */

object GhcMod {
    var errorReported : Boolean = false

    fun getPath() : String {
        return HaskellSettings.getInstance().getState().ghcModPath!!
    }

    fun getModuleContent(module : String) : List<Pair<String, String?>> {
        try {
            val path = getPath()
            val text = ProcessRunner(null).executeOrFail(path, "browse", "-d", module)
            if (!text.contains(":Error:")) {
                val f: (String) -> Pair<String, String?> = {
                    if (it.contains("::")) {
                        val t = it.split("::")
                        Pair(t[0].trim(), t[1].trim())
                    } else {
                        Pair(it, null)
                    }
                }
                return text.split('\n').map(f).toList()
            } else {
                return listOf()
            }
        } catch(e : Exception) {
            reportError()
            return listOf()
        }

    }

    fun debug(basePath : String, file: String) : Map<String, String> {
        try {
            val path = getPath()
            val text = ProcessRunner(basePath).executeOrFail(path, "debug", file)
            if (!text.contains(":Error:")) {
                val map = HashMap<String, String>()
                for (line in text.split('\n')) {
                    val index = line.indexOf(":")
                    map.put(line.substring(0, index), line.substring(index).trim())
                }
                return map
            } else {
                return Collections.emptyMap()
            }
        } catch(e : Exception) {
            reportError()
            return Collections.emptyMap()
        }
    }

    fun check(basePath : String, file: String) : List<String> {
        try {
            val path = getPath()
            val text = ProcessRunner(basePath).executeOrFail(path, "check", file)
            if (!text.contains(":Error:")) {
                return text.split('\n').toList()
            } else {
                return listOf()
            }
        } catch(e : Exception) {
            reportError()
            return listOf()
        }

    }

    fun reportError() {
        if (!errorReported) {
            Notifications.Bus.notify(Notification("ghc-mod error", "ghc-mod", "Can't find ghc-mod executable. "+
            "Please correct ghc-mod path in settings.", NotificationType.ERROR))
            errorReported = true
        }
    }

    fun сheck() : Boolean {
        try {
            ProcessRunner(null).executeOrFail(getPath())
            return true;
        } catch(e : IOException) {
            return false;
        }
    }


    fun getModulesList() : List<String> {
        try {
            val text = ProcessRunner(null).executeOrFail(getPath(), "list")
            if (!text.contains(":Error:")) {
                return text.split('\n').toList()
            } else {
                return listOf()
            }
        } catch(e : Exception) {
            reportError()
            return listOf()
        }
    }

}