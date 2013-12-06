package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.Key;

import java.util.regex.Pattern;

final class HaskellConsoleHighlightingUtil {

    private static final String ID = "\\p{Lu}[\\p{Ll}\\p{Digit}]*";
    private static final String MODULE = ID + "(\\." + ID + ")*";
    private static final String MODULES = "(" + MODULE + "\\s*)*";
    private static final String PROMPT_ARROW = ">";
    static final String LINE_WITH_PROMPT = MODULES + PROMPT_ARROW + ".*";

    static final Pattern GHCI_PATTERN = Pattern.compile(MODULES + PROMPT_ARROW);

    static void processOutput(LanguageConsoleImpl console, String text, Key<?> attributes) {
        ConsoleViewContentType outputType = ConsoleViewContentType.NORMAL_OUTPUT;
        new LanguageConsoleViewImpl(console).print(text, outputType);
    }
}
