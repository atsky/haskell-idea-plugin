package org.jetbrains.yesod.lucius

/**
 * @author Leyla H
 */

import com.intellij.lang.Language

class LuciusLanguage : Language("Lucius", "text/lucius") {
    companion object {
        val INSTANCE: LuciusLanguage = LuciusLanguage()
    }
}