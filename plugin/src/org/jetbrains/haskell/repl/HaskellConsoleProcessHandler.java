package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;

import java.nio.charset.Charset;
import java.util.regex.Matcher;

public final class HaskellConsoleProcessHandler extends ColoredProcessHandler {

    private final LanguageConsoleImpl console;

    HaskellConsoleProcessHandler(Process process, String commandLine, LanguageConsoleImpl console) {
        super(process, commandLine, Charset.forName("UTF-8"));
        this.console = console;
    }



    @Override
    protected void textAvailable(String text, Key attributes) {
        String string = processPrompts(console, StringUtil.convertLineSeparators(text));
        HaskellConsoleHighlightingUtil.processOutput(console, string, attributes);
    }

    private static String processPrompts(LanguageConsoleImpl console, String text) {
        if (text != null && text.matches(HaskellConsoleHighlightingUtil.LINE_WITH_PROMPT)) {
            Matcher matcher = HaskellConsoleHighlightingUtil.GHCI_PATTERN.matcher(text);
            matcher.find();
            String prefix = matcher.group();
            String trimmed = StringUtil.trimStart(text, prefix).trim();
            console.setPrompt(prefix + " ");
            return trimmed;
        }
        return text;
    }

    public LanguageConsoleImpl getLanguageConsole() {
        return console;
    }
}
