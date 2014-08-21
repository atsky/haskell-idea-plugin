package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.haskell.fileType.HaskellFileType;

public final class HaskellConsole extends LanguageConsoleImpl {

    private final ConsoleHistoryModel historyModel;
    private HaskellConsoleExecuteActionHandler executeHandler;

    HaskellConsole(Project project,
                   String title,
                   ConsoleHistoryModel historyModel) {
        super(project, title, HaskellFileType.INSTANCE.getLanguage());
        this.historyModel = historyModel;
    }


    ConsoleHistoryModel getHistoryModel() {
        return historyModel;
    }

    public HaskellConsoleExecuteActionHandler getExecuteHandler() {
        return executeHandler;
    }

    public void setExecuteHandler(HaskellConsoleExecuteActionHandler executeHandler) {
        this.executeHandler = executeHandler;
    }
}
