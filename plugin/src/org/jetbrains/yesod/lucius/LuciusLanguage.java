package org.jetbrains.yesod.lucius;

/**
 * @author Leyla H
 */

import com.intellij.lang.Language;

public class LuciusLanguage extends Language {
    public static final LuciusLanguage INSTANCE = new LuciusLanguage();

    public LuciusLanguage() {
        super("Lucius", "text/lucius");
    }
}