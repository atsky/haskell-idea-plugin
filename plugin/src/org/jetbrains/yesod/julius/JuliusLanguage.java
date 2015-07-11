package org.jetbrains.yesod.julius;

/**
 * @author Leyla H
 */

import com.intellij.lang.Language;

public class JuliusLanguage extends Language {
    public static final JuliusLanguage INSTANCE = new JuliusLanguage();

    public JuliusLanguage() {
        super("Julius", "text/julius");
    }
}