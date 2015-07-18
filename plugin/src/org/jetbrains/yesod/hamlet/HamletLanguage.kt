package org.jetbrains.yesod.hamlet

/**
 * @author Leyla H
 */

import com.intellij.lang.Language

public class HamletLanguage : Language("Hamlet", "text/hamlet") {
    companion object {
        public val INSTANCE: HamletLanguage = HamletLanguage()
    }
}