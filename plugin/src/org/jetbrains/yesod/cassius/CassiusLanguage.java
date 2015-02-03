package org.jetbrains.yesod.cassius;

/**
 * @author Leyla H
 */

import com.intellij.lang.Language;

public class CassiusLanguage extends Language {
    public static final CassiusLanguage INSTANCE = new CassiusLanguage();

    public CassiusLanguage() {
        super("Cassius", "text/cassius");
    }
}