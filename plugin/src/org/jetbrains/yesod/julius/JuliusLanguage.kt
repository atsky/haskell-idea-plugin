package org.jetbrains.yesod.julius

/**
 * @author Leyla H
 */

import com.intellij.lang.Language

class JuliusLanguage : Language("Julius", "text/julius") {
    companion object {
        val INSTANCE: JuliusLanguage = JuliusLanguage()
    }
}