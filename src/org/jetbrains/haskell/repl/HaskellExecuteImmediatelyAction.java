package org.jetbrains.haskell.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

final class HaskellExecuteImmediatelyAction extends HaskellExecuteActionBase {

    HaskellExecuteImmediatelyAction(HaskellConsole languageConsole,
                                    ProcessHandler processHandler,
                                    HaskellConsoleExecuteActionHandler executeHandler) {
        super(languageConsole, processHandler, executeHandler, HaskellConsoleRunner.EXECUTE_ACTION_IMMEDIATELY_ID);
    }

    public void actionPerformed(AnActionEvent e) {
        executeHandler.runExecuteAction(console, true);
    }
}
