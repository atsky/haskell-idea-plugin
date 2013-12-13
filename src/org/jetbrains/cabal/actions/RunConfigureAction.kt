package org.jetbrains.cabal.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.cabal.CabalInterface
import java.io.IOException
import org.jetbrains.cabal.findCabal
import com.intellij.openapi.actionSystem.PlatformDataKeys

public class RunConfigureAction() : AnAction() {
    public override fun actionPerformed(e: AnActionEvent?): Unit {

        //val project = e!!.getProject()!!
        //e.getData(PlatformDataKeys.)
        //findCabal(project);

        //CabalInterface(project).configure()
    }


}
