package org.jetbrains.haskell.repl;


import java.util.regex.Pattern;

final class HaskellConsoleHighlightingUtil {

    private static final String ID = "\\p{Lu}[\\p{Ll}\\p{Digit}]*";
    private static final String MODULE = "\\*?" + ID + "(\\." + ID + ")*";
    private static final String MODULES = "(" + MODULE + "\\s*)*";
    private static final String PROMPT_ARROW = ">";
    static final String LINE_WITH_PROMPT = MODULES + PROMPT_ARROW + ".*";

    static final Pattern GHCI_PATTERN = Pattern.compile(MODULES + PROMPT_ARROW);

}
