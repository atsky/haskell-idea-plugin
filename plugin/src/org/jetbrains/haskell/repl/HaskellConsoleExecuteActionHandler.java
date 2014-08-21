package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;

public abstract class HaskellConsoleExecuteActionHandler {

    private final Project project;
    private final boolean preserveMarkup;

    public HaskellConsoleExecuteActionHandler(Project project,
                                       boolean preserveMarkup) {
        this.project = project;
        this.preserveMarkup = preserveMarkup;
    }

    public abstract void processLine(String line);

    public void runExecuteAction(final HaskellConsole console,
                                 boolean executeImmediately) {
        ConsoleHistoryModel consoleHistoryModel = console.getHistoryModel();
        if (executeImmediately) {
            execute(console, consoleHistoryModel);
            return;
        }

        // Process input and add to history
        Editor editor = console.getCurrentEditor();
        Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();
        final int offset = caretModel.getOffset();
        String text = document.getText();

        if (!"".equals(text.substring(offset).trim())) {
            String before = text.substring(0, offset);
            String after = text.substring(offset);
            final int indent = 0;
            String spaces = StringUtil.repeatSymbol(' ', indent);
            final String newText = before + "\n" + spaces + after;

            new WriteCommandAction(project) {
                @Override
                protected void run(Result result) throws Throwable {
                    console.setInputText(newText);
                    caretModel.moveToOffset(offset + indent + 1);
                }
            }.execute();

            return;
        }

        execute(console, consoleHistoryModel);
    }

    private void execute(LanguageConsoleImpl languageConsole,
                         ConsoleHistoryModel consoleHistoryModel) {
        // Process input and add to history
        Document document = languageConsole.getCurrentEditor().getDocument();
        String text = document.getText();
        TextRange range = new TextRange(0, document.getTextLength());

        languageConsole.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
        languageConsole.addCurrentToHistory(range, false, preserveMarkup);
        languageConsole.setInputText("");
        if (!StringUtil.isEmptyOrSpaces(text)) {
            consoleHistoryModel.addToHistory(text);
        }
        // Send to interpreter / server
        processLine(text);
    }
}
