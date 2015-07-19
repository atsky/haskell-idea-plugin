package org.jetbrains.yesod.julius

/**
 * @author Leyla H
 */

import com.intellij.lang.Language

public class JuliusLanguage : Language("Julius", "text/julius") {
    companion object {
        public val INSTANCE: JuliusLanguage = JuliusLanguage()
    }
}