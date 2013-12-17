package org.jetbrains.haskell.repl;

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.EmptyAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.haskell.icons.HaskellIcons;

import javax.swing.*;

abstract class HaskellExecuteActionBase extends DumbAwareAction {

    private static final Icon ICON = HaskellIcons.DEFAULT;

    protected final HaskellConsole console;
    protected final ProcessHandler processHandler;
    protected final HaskellConsoleExecuteActionHandler executeHandler;

    protected HaskellExecuteActionBase(HaskellConsole languageConsole,
                                       ProcessHandler processHandler,
                                       HaskellConsoleExecuteActionHandler executeHandler,
                                       String actionId) {
        super(null, null, ICON);
        this.console = languageConsole;
        this.processHandler = processHandler;
        this.executeHandler = executeHandler;
        EmptyAction.setupAction(this, actionId, null);
    }

    public void update(AnActionEvent e) {
        EditorEx editor = console.getConsoleEditor();
        Lookup lookup = LookupManager.getActiveLookup(editor);
        e.getPresentation().setEnabled(!processHandler.isProcessTerminated() &&
            (lookup == null || !lookup.isCompletion()));
    }
}
