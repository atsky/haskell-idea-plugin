package org.jetbrains.haskell.debugger.actions

import javax.swing.Icon
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

public abstract class SwitchableAction(text: String?, description: String?, icon: Icon?) : AnAction(text, description, icon) {

    public var enabled: Boolean = true

    override fun update(e: AnActionEvent?) {
        val p = e?.getPresentation()
        p?.setEnabled(enabled)
    }
}