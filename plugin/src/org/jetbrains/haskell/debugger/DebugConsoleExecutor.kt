package org.jetbrains.haskell.debugger

import com.intellij.execution.Executor
import javax.swing.Icon
import com.intellij.icons.AllIcons
import org.jetbrains.annotations.NonNls

class DebugConsoleExecutor : Executor() {
    companion object {
        val EXECUTOR_ID: String = "DebugConsole"
    }
    override fun getToolWindowId(): String? = "Run"
    override fun getToolWindowIcon(): Icon? = AllIcons.Toolwindows.ToolWindowDebugger
    override fun getIcon(): Icon = AllIcons.Debugger.Console
    override fun getDisabledIcon(): Icon? = null
    override fun getDescription(): String? = "Open debug REPL console"
    override fun getActionName(): String = "DebugConsole"
    override fun getId(): String = EXECUTOR_ID
    override fun getStartActionText(): String = "Open debug console"
    override fun getContextActionId(): String? = "Haskell.Debugger.OpenDebuggerConsole"
    override fun getHelpId(): String? = "debugging.DebugWindow"

}