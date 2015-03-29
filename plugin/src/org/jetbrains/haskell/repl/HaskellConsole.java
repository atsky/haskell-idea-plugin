package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.haskell.fileType.HaskellFileType;

public final class HaskellConsole extends LanguageConsoleImpl {
    private HaskellConsoleExecuteActionHandler executeHandler;

    public HaskellConsole(Project project,
                   String title) {
        super(project, title, HaskellFileType.INSTANCE.getLanguage());
    }


    public HaskellConsoleExecuteActionHandler getExecuteHandler() {
        return executeHandler;
    }

    public void registerExecuteActionHandler(HaskellConsoleExecuteActionHandler executeHandler,
                                             ProcessHandler processHandler) {
        setExecuteHandler(executeHandler);
        AnAction enterAction = new HaskellConsoleEnterAction(this, processHandler, executeHandler);
        enterAction.registerCustomShortcutSet(enterAction.getShortcutSet(), this.getConsoleEditor().getComponent());
    }

    public void setExecuteHandler(HaskellConsoleExecuteActionHandler executeHandler) {
        this.executeHandler = executeHandler;
    }
}
