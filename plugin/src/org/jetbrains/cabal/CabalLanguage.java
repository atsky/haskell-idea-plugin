package org.jetbrains.cabal;

import com.intellij.lang.Language;
import kotlin.jvm.JvmField;

public class CabalLanguage extends Language {
    @JvmField
    public static final CabalLanguage INSTANCE = new CabalLanguage();

    public CabalLanguage() {
        super("Cabal", "text/cabal");
    }
}