package org.jetbrains.haskell.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

public final class HaskellConsoleEnterAction extends HaskellExecuteActionBase {

    public HaskellConsoleEnterAction(HaskellConsole languageConsole,
                                     ProcessHandler processHandler,
                                     HaskellConsoleExecuteActionHandler executeHandler) {
        super(languageConsole, processHandler, executeHandler, HaskellConsoleRunner.EXECUTE_ACTION_ID);
    }

    public void actionPerformed(AnActionEvent e) {
        executeHandler.runExecuteAction(console, false);
    }
}
