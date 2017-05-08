package org.jetbrains.haskell.debugger.highlighting

import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.impl.ui.ExecutionPointHighlighter
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import org.jetbrains.haskell.debugger.parser.HsFilePosition

/**
 * Manages code highlighting process within debug session
 *
 * @author Habibullin Marat
 */
class HsDebugSessionListener(val debugSession: XDebugSession) : XDebugSessionListener {
    val highlighter = HsExecutionPointHighlighter(debugSession.project)

    override fun sessionPaused() = highlightCurrentFrame(false)
    override fun sessionResumed() = highlighter.hide()
    override fun sessionStopped() = highlighter.hide()
    override fun stackFrameChanged() = highlightCurrentFrame(true)
    override fun beforeSessionResume() {}

    private fun highlightCurrentFrame(useSelection: Boolean) {
        val currentStackFrame = debugSession.currentStackFrame
        if (currentStackFrame !is HsStackFrame) {
            println("WARNING, HsDebugSessionListener, sessionPaused: debugSession.getCurrentStackFrame() returned not HsStackFrame instance")
        } else {
            highlighter.show(currentStackFrame, useSelection, null)
        }
    }
}