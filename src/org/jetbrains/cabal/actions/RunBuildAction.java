package org.jetbrains.cabal.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.cabal.CabalInterface;

import java.io.IOException;

public final class RunBuildAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        new CabalInterface(e.getProject()).build();
    }
}