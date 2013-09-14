package org.jetbrains.haskell.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.haskell.fileType.HaskellFile;

import java.io.File;

public final class LoadHaskellFileInConsoleAction extends HaskellConsoleActionBase {

    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(DataKeys.EDITOR);
        if (editor == null)
            return;
        Project project = editor.getProject();
        if (project == null)
            return;
        Document document = editor.getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null || !(psiFile instanceof HaskellFile))
            return;
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null)
            return;
        String filePath = virtualFile.getPath();
        if (filePath == null)
            return;

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();

        String command = ":load \"" + filePath + "\"";
        executeCommand(project, command);
    }

    private static String getActionFile(AnActionEvent e) {
        Module m = RunHaskellConsoleAction.getModule(e);
        if (m == null)
            return null;
        Editor editor = e.getData(DataKeys.EDITOR);
        if (editor == null || editor.getProject() == null)
            return null;
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        if (psiFile == null || !(psiFile instanceof HaskellFile))
            return null;
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || virtualFile instanceof LightVirtualFile)
            return null;
        return virtualFile.getPath();
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        String filePath = getActionFile(e);
        if (filePath == null) {
            presentation.setVisible(false);
        } else {
            File f = new File(filePath);
            presentation.setVisible(true);
            presentation.setText(String.format("Load \"%s\" in Haskell REPL", f.getName()));
        }
    }
}
