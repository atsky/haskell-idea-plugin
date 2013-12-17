package org.jetbrains.cabal;

import com.intellij.lang.Language;

public class CabalLanguage extends Language {
    public static final CabalLanguage INSTANCE = new CabalLanguage();

    public CabalLanguage() {
        super("Cabal", "text/cabal");
    }
}