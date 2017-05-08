package org.jetbrains.haskell.debugger.history

import org.jetbrains.haskell.debugger.actions.SwitchableAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.openapi.actionSystem.DefaultActionGroup
import org.jetbrains.haskell.debugger.highlighting.HsExecutionPointHighlighter
import com.intellij.ui.AppUIUtil
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import java.util.ArrayList
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.parser.HsHistoryFrameInfo
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.icons.AllIcons.Actions
import java.util.Deque
import java.util.ArrayDeque
import com.intellij.xdebugger.impl.ui.XDebuggerUIConstants
import org.jetbrains.haskell.debugger.utils.SyncObject
import com.intellij.xdebugger.impl.XDebugSessionImpl
import com.intellij.xdebugger.XDebugSession

/**
 * Created by vlad on 8/5/14.
 */

class HistoryManager(private val debugSession : XDebugSession,
                            private val debugProcess: HaskellDebugProcess) {
    val HISTORY_SIZE: Int = 20

    class StackState(val historyIndex: Int,
                            val realHistIndex: Int,
                            val allFramesCollected: Boolean,
                            val historyFrames: List<HsHistoryFrame>,
                            val historyFramesLines: List<*>)


    private val historyStack: HsHistoryStack = HsHistoryStack(debugProcess)

    private var historyPanel: HistoryTab? = null
    private val historyHighlighter = HsExecutionPointHighlighter(debugProcess.session!!.project,
            HsExecutionPointHighlighter.HighlighterType.HISTORY)
    private val backAction: SwitchableAction = object : SwitchableAction("back", "Move back along history", Actions.Back) {
        override fun actionPerformed(e: AnActionEvent?) {
            enabled = false
            forwardAction.enabled = false
            update(e)
            forwardAction.update(e)
            if (historyStack.historyIndex - 1 > HISTORY_SIZE) {
                historyStack.moveTo(historyStack.historyIndex + 1)
            } else {
                historyPanel?.shiftBack()
            }
        }
    }
    private val forwardAction: SwitchableAction = object : SwitchableAction("forward", "Move forward along history",
            Actions.Forward) {
        override fun actionPerformed(e: AnActionEvent?) {
            enabled = false
            backAction.enabled = false
            update(e)
            backAction.update(e)
            if (historyStack.historyIndex > HISTORY_SIZE) {
                historyStack.moveTo(historyStack.historyIndex - 1)
            } else {
                historyPanel?.shiftForward()
            }
        }
    }

    fun initHistoryTab(debugSession : XDebugSessionImpl) {
        historyPanel = HistoryTab(debugSession, debugProcess, this)
    }

    fun withRealFrameUpdate(finalCallback: ((MoveHistResult?) -> Unit)?): Unit =
            historyStack.withRealFrameUpdate(finalCallback)

    fun indexSelected(index: Int): Unit = historyStack.moveTo(index)

    fun setHistoryFramesInfo(initial: HsHistoryFrameInfo, others: ArrayList<HsHistoryFrameInfo>, full: Boolean) {
        AppUIUtil.invokeLaterIfProjectAlive(debugProcess.session!!.project, Runnable({ ->
            historyPanel!!.addHistoryLine(initial.toString())
            for (info in others) {
                historyPanel!!.addHistoryLine(info.toString())
            }
            if (!full) {
                historyPanel!!.addHistoryLine("...")
            }
        }))
    }

    fun registerContent(ui: RunnerLayoutUi) {
        initHistoryTab(debugSession as XDebugSessionImpl)
        val context = ui.createContent("history", historyPanel!!.getComponent(), "History", null, null)
        context.isCloseable = false
        ui.addContent(context)
        ui.options.setToFocus(context, XDebuggerUIConstants.LAYOUT_VIEW_BREAKPOINT_CONDITION)
    }

    fun registerActions(topToolbar: DefaultActionGroup) {
        topToolbar.addSeparator()
        topToolbar.add(backAction)
        topToolbar.add(forwardAction)
    }

    fun historyChanged(hasNext: Boolean, hasPrevious: Boolean, stackFrame: HsStackFrame?) {
        AppUIUtil.invokeLaterIfProjectAlive(debugProcess.session!!.project, Runnable({ ->
            backAction.enabled = hasPrevious
            forwardAction.enabled = hasNext
            historyPanel!!.stackChanged(stackFrame)
            if (stackFrame != null) {
                historyHighlighter.show(stackFrame, false, null)
            } else {
                historyHighlighter.hide()
            }
        }))
    }

    fun clean(): Unit = historyChanged(false, false, null)

    fun historyFrameAppeared(frame: HsHistoryFrame): Unit = historyStack.addFrame(frame)

    fun resetHistoryStack(): Unit = historyStack.clear()

    fun markHistoryFramesAsObsolete(): Unit = historyStack.markFramesAsObsolete()

    // save/load state managing
    private val states: Deque<StackState> = ArrayDeque()
    fun saveState(): Unit = states.addLast(historyStack.save())
    fun loadState(): Unit = AppUIUtil.invokeLaterIfProjectAlive(debugProcess.session!!.project, {
        historyStack.loadFrom(states.pollLast()!!)
    })
    fun hasSavedStates(): Boolean = !states.isEmpty()

    private inner class HsHistoryStack(private val debugProcess: HaskellDebugProcess) {
        var historyIndex: Int = 0
            private set
        private var realHistIndex: Int = 0
        private var allFramesCollected = false
        private val historyFrames: java.util.ArrayList<HsHistoryFrame> = ArrayList()

        fun addFrame(frame: HsHistoryFrame) {
            historyFrames.add(frame)
        }

        fun clear() {
            historyIndex = 0
            realHistIndex = 0
            allFramesCollected = false
            historyFrames.clear()
            updateHistory()
        }

        fun hasNext(): Boolean = historyIndex > 0

        fun hasPrevious(): Boolean = !allFramesCollected || historyIndex + 1 < historyFrames.size

        fun currentFrame(): HsHistoryFrame? =
                if (historyIndex < historyFrames.size) historyFrames.get(historyIndex) else null

        fun moveTo(index: Int) {
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
                if (historyFrames[historyIndex - 1].obsolete) {
                    val syncObject = SyncObject()
                    syncObject.lock()
                    withRealFrameUpdate {
                        debugProcess.debugger.forward(object : CommandCallback<MoveHistResult?>() {
                            override fun execAfterParsing(result: MoveHistResult?) {
                                syncObject.lock()
                                if (result != null) {
                                    --historyIndex
                                    --realHistIndex
                                }
                                syncObject.signal()
                                syncObject.unlock()
                            }
                        })
                    }
                    syncObject.await()
                    syncObject.unlock()
                } else {
                    --historyIndex
                }
            }
        }

        private fun moveBack() {
            if (historyIndex + 1 < historyFrames.size) {
                if (historyFrames[historyIndex + 1].obsolete) {
                    val syncObject = SyncObject()
                    syncObject.lock()
                    withRealFrameUpdate {
                        debugProcess.debugger.back(object : CommandCallback<MoveHistResult?>() {
                            override fun execAfterParsing(result: MoveHistResult?) {
                                syncObject.lock()
                                if (result != null) {
                                    ++historyIndex
                                    ++realHistIndex
                                }
                                syncObject.signal()
                                syncObject.unlock()
                            }
                        })
                    }
                    syncObject.await()
                    syncObject.unlock()
                } else {
                    ++historyIndex
                }
            } else if (allFramesCollected) {
            } else {
                val syncObject = SyncObject()
                syncObject.lock()
                withRealFrameUpdate {
                    debugProcess.debugger.back(object : CommandCallback<MoveHistResult?>() {
                        override fun execAfterParsing(result: MoveHistResult?) {
                            syncObject.lock()
                            if (result != null) {
                                val frame = HsHistoryFrame(debugProcess.debugger, HsStackFrameInfo(result.filePosition, result.bindingList.list, null))
                                addFrame(frame)
                                ++historyIndex
                                ++realHistIndex
                            } else {
                                allFramesCollected = true
                            }
                            syncObject.signal()
                            syncObject.unlock()
                        }
                    })
                }
                syncObject.await()
                syncObject.unlock()
            }
        }

        private fun updateHistory() = historyChanged(hasNext(), hasPrevious(), currentFrame())

        fun markFramesAsObsolete() {
            for (frame in historyFrames) {
                frame.obsolete = true
            }
        }

        fun withRealFrameUpdate(finalCallback: ((MoveHistResult?) -> Unit)?) {
            if (realHistIndex == historyIndex) {
                finalCallback?.invoke(null)
                return
            }
            if (realHistIndex < historyIndex) {
                debugProcess.debugger.back(SequentialBackCallback(historyIndex - realHistIndex, finalCallback))
            } else {
                debugProcess.debugger.forward(SequentialForwardCallback(realHistIndex - historyIndex, finalCallback))
            }
        }

        fun save(): StackState = StackState(historyIndex, realHistIndex, allFramesCollected, ArrayList(historyFrames),
                historyPanel!!.getHistoryFramesModel().elements)

        fun loadFrom(state: StackState) {
            historyIndex = state.historyIndex
            realHistIndex = state.realHistIndex
            allFramesCollected = state.allFramesCollected
            historyFrames.clear()
            historyFrames.addAll(state.historyFrames)
            historyPanel!!.getHistoryFramesModel().removeAllElements()
            for (elem in state.historyFramesLines) {
                historyPanel!!.addHistoryLine(elem.toString())
            }
        }

        private inner class SequentialBackCallback(var toGo: Int,
                                                   val finalCallback: ((MoveHistResult?) -> Unit)?) : CommandCallback<MoveHistResult?>() {
            override fun execAfterParsing(result: MoveHistResult?) {
                --toGo
                ++realHistIndex
                if (toGo == 0 || result == null) {
                    finalCallback?.invoke(null)
                } else {
                    debugProcess.debugger.back(this)
                }
            }
        }

        private inner class SequentialForwardCallback(var toGo: Int,
                                                      val finalCallback: ((MoveHistResult?) -> Unit)?) : CommandCallback<MoveHistResult?>() {
            override fun execAfterParsing(result: MoveHistResult?) {
                --toGo
                --realHistIndex
                if (toGo == 0 || result == null) {
                    finalCallback?.invoke(null)
                } else {
                    debugProcess.debugger.forward(this)
                }
            }
        }
    }
}
