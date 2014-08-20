package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.parser.ParseResult
import com.intellij.execution.ui.ConsoleViewContentType
import org.jetbrains.haskell.debugger.procdebuggers.utils.CommandQueue

public abstract class QueueDebugger(public val debugProcess: HaskellDebugProcess,
                                    private val showCommandsInConsole: Boolean) : ProcessDebugger {
    protected var lastCommand: AbstractCommand<out ParseResult?>? = null
        private set
    protected var debugStarted: Boolean = false
        private set

    private val writeLock = ReentrantLock()
    private val queue: CommandQueue;

    {
        queue = CommandQueue({(command: AbstractCommand<out ParseResult?>) -> execute(command) })
        queue.start()
    }

    override fun isReadyForNextCommand(): Boolean = !queue.someCommandInProgress()

    final override fun enqueueCommand(command: AbstractCommand<*>) = queue.addCommand(command)

    final override fun close() {
        queue.stop()
        doClose()
    }

    protected open fun doClose() {}

    protected fun enqueueCommandWithPriority(command: AbstractCommand<*>): Unit = queue.addCommand(command, true)

    protected fun setReadyForInput(): Unit = queue.setReadyForInput()

    /**
     * Executes command immediately
     */
    protected fun execute(command: AbstractCommand<out ParseResult?>) {
        val text = command.getText()
        writeLock.lock()
        lastCommand = command
        command.callback?.execBeforeSending()
        printCommandIfNeeded(text)
        sendCommandToProcess(text)
        if (lastCommand is TraceCommand) {
            debugStarted = true
        }
        writeLock.unlock()
    }

    private fun printCommandIfNeeded(text: String) {
        if (lastCommand !is HiddenCommand) {
            if (showCommandsInConsole) {
                debugProcess.printToConsole(text, ConsoleViewContentType.SYSTEM_OUTPUT)
            } else {
                print(text)
            }
        }
    }

    private fun sendCommandToProcess(text: String) {
        val os = debugProcess.getProcessHandler().getProcessInput()!!
        os.write(text.toByteArray())
        os.flush()
    }
}