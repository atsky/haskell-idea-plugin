package org.jetbrains.haskell.debugger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

/**
 * Created by vlad on 8/4/14.
 */

public abstract class SwitchableAction(text: String?, description: String?, icon: Icon?) : AnAction(text, description, icon) {

    public var enabled: Boolean = true

    override fun update(e: AnActionEvent?) {
        val p = e?.getPresentation()
        p?.setEnabled(enabled)
    }
}