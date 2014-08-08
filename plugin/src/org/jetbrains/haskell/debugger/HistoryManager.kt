package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.actions.SwitchableAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.xdebugger.ui.XDebugTabLayouter
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.openapi.actionSystem.DefaultActionGroup
import org.jetbrains.haskell.debugger.highlighting.HsExecutionPointHighlighter
import com.intellij.ui.AppUIUtil
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import java.util.ArrayList
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.protocol.BackCommand
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.parser.HsHistoryFrameInfo

/**
 * Created by vlad on 8/5/14.
 */

public class HistoryManager(private val debugProcess: HaskellDebugProcess) : XDebugTabLayouter() {
    class object {
        public val HISTORY_SIZE: Int = 20
    }
    public val historyStack: HsHistoryStack = HsHistoryStack(debugProcess)
    private val realHistIndex: Int = 0

    private val historyPanel: HistoryPanel = HistoryPanel(debugProcess, this)
    private val historyHighlighter = HsExecutionPointHighlighter(debugProcess.getSession()!!.getProject(),
            HsExecutionPointHighlighter.HighlighterType.HISTORY)
    private val backAction: SwitchableAction = object : SwitchableAction("back", "Move back along history",
            com.intellij.icons.AllIcons.Actions.Back) {
        override fun actionPerformed(e: AnActionEvent?) {
            enabled = false
            forwardAction.enabled = false
            update(e)
            forwardAction.update(e)
            if (historyStack.historyIndex - 1 > HISTORY_SIZE) {
                historyStack.moveTo(historyStack.historyIndex + 1)
            } else {
                historyPanel.shiftBack()
            }
        }
    }
    private val forwardAction: SwitchableAction = object : SwitchableAction("forward", "Move forward along history",
            com.intellij.icons.AllIcons.Actions.Forward) {
        override fun actionPerformed(e: AnActionEvent?) {
            enabled = false
            backAction.enabled = false
            update(e)
            backAction.update(e)
            if (historyStack.historyIndex > HISTORY_SIZE) {
                historyStack.moveTo(historyStack.historyIndex - 1)
            } else {
                historyPanel.shiftForward()
            }
        }
    }

    public fun indexSelected(index: Int) {
        historyStack.moveTo(index)
    }

    public fun setHistoryFrameInfo(initial: HsHistoryFrameInfo, others: ArrayList<HsHistoryFrameInfo>, full: Boolean) {
        AppUIUtil.invokeLaterIfProjectAlive(debugProcess.getSession()!!.getProject(), Runnable({() ->
            historyPanel.addHistoryLine(initial.toString())
            for (info in others) {
                historyPanel.addHistoryLine(info.toString())
            }
            if (!full) {
                historyPanel.addHistoryLine("...")
            }
        }))
    }

    override fun registerAdditionalContent(ui: RunnerLayoutUi) {
        val context = ui.createContent("history", historyPanel, "History", null, null)
        ui.addContent(context)
    }

    public fun registerActions(leftToolbar: DefaultActionGroup, topToolbar: DefaultActionGroup) {
        topToolbar.addSeparator()
        topToolbar.add(backAction)
        topToolbar.add(forwardAction)
    }

    public fun historyChanged(hasNext: Boolean, hasPrevious: Boolean, stackFrame: HsStackFrame?) {
        AppUIUtil.invokeLaterIfProjectAlive(debugProcess.getSession()!!.getProject(), Runnable({() ->
            backAction.enabled = hasPrevious
            forwardAction.enabled = hasNext
            historyPanel.stackChanged(stackFrame)
            if (stackFrame != null) {
                historyHighlighter.show(stackFrame, false, null)
            } else {
                historyHighlighter.hide()
            }
        }))
    }

    public fun clean() {
        AppUIUtil.invokeLaterIfProjectAlive(debugProcess.getSession()!!.getProject(), Runnable({() ->
            backAction.enabled = false
            forwardAction.enabled = false
            historyPanel.stackChanged(null)
            historyHighlighter.hide()
        }))
    }

    public inner class HsHistoryStack(private val debugProcess: HaskellDebugProcess) {
        public var historyIndex: Int = 0
            private set
        private var allFramesCollected = false
        private val historyFrames: ArrayList<HsHistoryFrame> = ArrayList()

        public fun addFrame(frame: HsHistoryFrame) {
            historyFrames.add(frame)
        }

        public fun clear() {
            historyIndex = 0
            allFramesCollected = false
            historyFrames.clear()
            updateHistory()
        }

        public fun hasNext(): Boolean {
            return historyIndex > 0
        }

        public fun hasPrevious(): Boolean {
            return !allFramesCollected || historyIndex + 1 < historyFrames.size
        }

        public fun currentFrame(): HsHistoryFrame? {
            return if (historyIndex < historyFrames.size) historyFrames.get(historyIndex) else null
        }

        public fun moveTo(index: Int) {
            if (index == -1 || index == historyIndex) {
            } else if (index < historyIndex) {
                val it = historyIndex - index
                for (i in 1..it) {
                    moveForward()
                }
            } else {
                val it = index - historyIndex
                for (i in 1..it) {
                    moveBack()
                }
            }
            updateHistory()
        }

        private fun moveForward() {
            if (historyIndex > 0) {
                --historyIndex
                debugProcess.debugger.forward()
            }
        }

        private fun moveBack() {
            if (historyIndex + 1 < historyFrames.size) {
                ++historyIndex
                debugProcess.debugger.back(BackCommand(null))
            } else if (allFramesCollected) {
            } else {
                debugProcess.debugger.back(BackCommand(object : CommandCallback<MoveHistResult?>() {
                    override fun execAfterParsing(result: MoveHistResult?) {
                        if (result != null) {
                            val frame = HsHistoryFrame(debugProcess, HsStackFrameInfo(result.filePosition, result.bindingList.list, null))
                            addFrame(frame)
                            ++historyIndex
                        } else {
                            allFramesCollected = true
                        }
                    }
                }))
            }
        }

        private fun updateHistory() {
            historyChanged(hasNext(), hasPrevious(), currentFrame())
        }

        public fun markFramesAsObsolete() {
            for (frame in historyFrames) {
                frame.obsolete = true
            }
        }
    }
}