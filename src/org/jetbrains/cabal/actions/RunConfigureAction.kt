package org.jetbrains.cabal.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.cabal.CabalInterface
import java.io.IOException

public class RunConfigureAction() : AnAction() {
    public override fun actionPerformed(e: AnActionEvent?): Unit {
        CabalInterface(e!!.getProject()!!).configure()
    }


}
