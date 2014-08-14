package org.jetbrains.haskell.repl.actions;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.fileType.HaskellFile;
import org.jetbrains.haskell.repl.*;

import java.util.Collection;

abstract class HaskellConsoleActionBase extends AnAction {

    private static final class HaskellConsoleMatcher implements NotNullFunction<RunContentDescriptor, Boolean> {

        @NotNull
        public Boolean fun(RunContentDescriptor descriptor) {
            return descriptor != null && (descriptor.getExecutionConsole() instanceof HaskellConsoleView);
        }
    }

    private static HaskellConsoleProcessHandler findRunningHaskellConsole(Project project) {
        Collection<RunContentDescriptor> descriptors = ExecutionHelper.findRunningConsole(project, new HaskellConsoleMatcher());
        for (RunContentDescriptor descriptor : descriptors) {
            ProcessHandler handler = descriptor.getProcessHandler();
            if (handler instanceof HaskellConsoleProcessHandler) {
                return (HaskellConsoleProcessHandler) handler;
            }
        }
        return null;
    }

    protected static void executeCommand(Project project, String command) {
            HaskellConsoleProcessHandler processHandler = findRunningHaskellConsole(project);

        // if a console isn't runnning, start one
        if (processHandler == null || processHandler.isProcessTerminated()) {
            Module module = RunHaskellConsoleAction.getModule(project);
            processHandler = HaskellConsoleRunner.run(module);
            if (processHandler == null)
                return;
        }

        // implement a command
        LanguageConsoleImpl languageConsole = processHandler.getLanguageConsole();
        languageConsole.setInputText(command);

        Editor editor = languageConsole.getCurrentEditor();
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(command.length());

        HaskellConsole console = (HaskellConsole) languageConsole;
        HaskellConsoleExecuteActionHandler handler = console.getExecuteHandler();

        handler.runExecuteAction(console, true);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        Editor editor = e.getData(DataKeys.EDITOR);

        if (editor == null) {
            presentation.setEnabled(false);
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }

        Document document = editor.getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null || !(psiFile instanceof HaskellFile)) {
            presentation.setEnabled(false);
            return;
        }

        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || virtualFile instanceof LightVirtualFile) {
            presentation.setEnabled(false);
            return;
        }

        HaskellConsoleProcessHandler handler = findRunningHaskellConsole(project);
        if (handler == null) {
            presentation.setEnabled(false);
            return;
        }

        LanguageConsoleImpl console = handler.getLanguageConsole();
        if (!(console instanceof HaskellConsole)) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setEnabled(true);
    }
}
