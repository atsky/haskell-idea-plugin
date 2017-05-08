package org.jetbrains.yesod.hamlet

/**
 * @author Leyla H
 */

import com.intellij.lang.Language

class HamletLanguage : Language("Hamlet", "text/hamlet") {
    companion object {
        val INSTANCE: HamletLanguage = HamletLanguage()
    }
}