package org.jetbrains.haskell.debugger.actions

import javax.swing.Icon
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class SwitchableAction(text: String?, description: String?, icon: Icon?) : AnAction(text, description, icon) {

    var enabled: Boolean = true

    override fun update(e: AnActionEvent?) {
        val p = e?.presentation
        p?.isEnabled = enabled
    }
}