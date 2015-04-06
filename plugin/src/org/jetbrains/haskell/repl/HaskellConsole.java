package org.jetbrains.haskell.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.haskell.fileType.HaskellFileType;

public final class HaskellConsole extends LanguageConsoleImpl {
    public HaskellConsole(Project project,
                   String title) {
        super(project, title, HaskellFileType.INSTANCE.getLanguage());
    }

}
