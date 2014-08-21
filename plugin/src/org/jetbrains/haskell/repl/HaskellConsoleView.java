package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public final class HaskellConsoleView extends LanguageConsoleViewImpl {

    public HaskellConsoleView(Project project,
                              String title,
                              ConsoleHistoryModel historyModel) {
        super(new HaskellConsole(project, title, historyModel));
    }

    @NotNull
    @Override
    public HaskellConsole getConsole() {
        return (HaskellConsole) super.getConsole();
    }
}
