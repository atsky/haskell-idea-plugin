package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
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

    public void registerExecuteActionHandler(HaskellConsoleExecuteActionHandler executeHandler,
                                             ProcessHandler processHandler) {
        getConsole().setExecuteHandler(executeHandler);
        AnAction enterAction = new HaskellConsoleEnterAction(getConsole(), processHandler, executeHandler);
        enterAction.registerCustomShortcutSet(enterAction.getShortcutSet(), getConsole().getConsoleEditor().getComponent());
    }
}
