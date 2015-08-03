package org.jetbrains.yesod.lucius

/**
 * @author Leyla H
 */

import com.intellij.lang.Language

public class LuciusLanguage : Language("Lucius", "text/lucius") {
    companion object {
        public val INSTANCE: LuciusLanguage = LuciusLanguage()
    }
}