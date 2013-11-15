package org.jetbrains.cabal.tool;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.cabal.CabalInterface;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


public class CabalToolWindowFactory implements ToolWindowFactory {
    private ToolWindow myToolWindow;
    private Project myProject;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myProject = project;
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createToolWindowPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private JComponent createToolWindowPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getToolbar(), BorderLayout.PAGE_START);
        ArrayList<String> list = new ArrayList<String>();
        list.add("test1");
        list.add("test2");
        JBList packages = new JBList(list);
        panel.add(packages, BorderLayout.CENTER);
        return panel;
    }

    private JComponent getToolbar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JButton(new AbstractAction("Configure") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CabalInterface(myProject).configure();
            }
        }));

        panel.add(new JButton(new AbstractAction("Build") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CabalInterface(myProject).build();
            }
        }));

        panel.add(new JButton(new AbstractAction("Clean") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CabalInterface(myProject).clean();
            }
        }));
        return panel;
    }


}