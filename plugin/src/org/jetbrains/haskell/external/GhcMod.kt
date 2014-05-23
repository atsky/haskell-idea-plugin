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


/**
 * Created by atsky on 3/29/14.
 */

public val GHC_MOD : GhcMod = GhcMod()

class GhcMod {
    var errorReported : Boolean = false

    fun getPath() : String {
        return HaskellSettings.getInstance().getState().ghcModPath!!
    }

    fun getModuleContent(module : String) : List<String> {
        try {
            val path = getPath()
            val text = ProcessRunner(null).execute(listOf(path, "browse", module))
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
            ProcessBuilder(listOf(getPath(), "version")).start().waitFor()
            return true;
        } catch(e : IOException) {
            return false;
        }
    }


    fun getModulesList() : List<String> {
        try {
            val text = ProcessRunner(null).execute(listOf(getPath(), "list"))
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