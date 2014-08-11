package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import com.intellij.execution.ui.ConsoleViewContentType
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.protocol.RemoveBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.FlowCommand

/**
 * Created by vlad on 8/8/14.
 */

public abstract class QueueDebugger(public val debugProcess: HaskellDebugProcess) : ProcessDebugger {
    private val writeLock = ReentrantLock()
    private val queue: CommandQueue;

    {
        queue = CommandQueue({(command: AbstractCommand<out ParseResult?>) -> execute(command) })
        queue.start()
    }

    protected var lastCommand: AbstractCommand<out ParseResult?>? = null
        private set
    protected var debugStarted: Boolean = false
        private set

    /**
     * Executes command immediately
     */
    protected fun execute(command: AbstractCommand<out ParseResult?>) {
        val text = command.getText()

        writeLock.lock()
        lastCommand = command
        command.callback?.execBeforeSending()
        if (lastCommand !is HiddenCommand) {
            debugProcess.printToConsole(text, ConsoleViewContentType.SYSTEM_OUTPUT)
        }
        val os = debugProcess.getProcessHandler().getProcessInput()!!
        os.write(text.toByteArray())
        os.flush()
        if (lastCommand is TraceCommand) {
            debugStarted = true
        }
        writeLock.unlock()
    }

    final override fun close() {
        queue.stop()
        doClose()
    }

    protected open fun doClose() {
    }

    final override fun enqueueCommand(command: AbstractCommand<*>) = queue.addCommand(command)

    protected fun enqueueCommandWithPriority(command: AbstractCommand<*>): Unit = queue.addCommand(command, true)

    protected fun setReadyForInput() {
        queue.setReadyForInput()
    }
}