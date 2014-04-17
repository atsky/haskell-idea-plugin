package org.jetbrains.haskell.external

import org.jetbrains.haskell.util.ProcessRunner
import java.io.IOException
import com.intellij.util.messages.MessageBus
import com.intellij.util.MessageBusUtil
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType

/**
 * Created by atsky on 3/29/14.
 */

public val GHC_MOD : GhcMod = GhcMod()

class GhcMod {
    var errorReported : Boolean = false

    val PROGRAM = System.getProperty("user.home") + "/.cabal/bin/ghc-mod"

    fun getModuleContent(module : String) : List<String> {
        try {
            val text = ProcessRunner(null).execute(listOf(PROGRAM, "browse", module))
            if (!text.contains(":Error:")) {
                return text.split('\n').toList()
            } else {
                return listOf()
            }
        } catch(e : Exception) {
            if (!errorReported) {
                Notifications.Bus.notify(Notification("ghc-mod error", "ghc-mod", "Can't find ghc-mod executable.", NotificationType.ERROR))
                errorReported = true
            }
            return listOf()
        }

    }

}