package org.jetbrains.cabal.tool;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Evgeny.Kurbatsky
 */
public class CabalMessageView {
    private final Project myProject;
    private final ConsoleView myConsole;


    public CabalMessageView(Project project, Process process) {
        myProject = project;
        TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        builder.addFilter(new GhcMessageFilter());
        myConsole = builder.getConsole();

        final OSProcessHandler osProcessHandler = new OSProcessHandler(process);
        myConsole.attachToProcess(osProcessHandler);
        osProcessHandler.startNotify();
    }

    public JComponent getComponent() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(myConsole.getComponent(), BorderLayout.CENTER);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAll(myConsole.createConsoleActions());


        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.COMPILER_MESSAGES_TOOLBAR, actionGroup, false);
        panel.add(toolbar.getComponent(), BorderLayout.LINE_START);
        return panel;
    }

    private final class GhcMessageFilter implements Filter {
        public Result applyFilter(String line, int entireLength) {
            int afterLineNumberIndex = line.indexOf(": "); // end of file_name_and_line_number sequence
            if (afterLineNumberIndex == -1) {
                return null;
            }

            String fileAndLineNumber = line.substring(0, afterLineNumberIndex);
            int index = fileAndLineNumber.lastIndexOf(':');

            if (index == -1) {
                return null;
            }

            final String fileName = fileAndLineNumber.substring(0, index);
            String lineNumberStr = fileAndLineNumber.substring(index + 1, fileAndLineNumber.length()).trim();
            int lineNumber;
            try {
                lineNumber = Integer.parseInt(lineNumberStr);
            }
            catch (NumberFormatException e) {
                return null;
            }

            final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(fileName.replace(File.separatorChar, '/'));
            if (file == null) {
                return null;
            }

            int textStartOffset = entireLength - line.length();
            int highlightEndOffset = textStartOffset + afterLineNumberIndex;

            OpenFileHyperlinkInfo info = new OpenFileHyperlinkInfo(myProject, file, lineNumber - 1);
            return new Result(textStartOffset, highlightEndOffset, info);
        }
    }
}
