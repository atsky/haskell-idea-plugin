package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.parser.ParseResult
import com.intellij.execution.ui.ConsoleViewContentType
import org.jetbrains.haskell.debugger.procdebuggers.utils.CommandQueue
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.process.ProcessHandler

public abstract class QueueDebugger(public val debugProcess: HaskellDebugProcess,
                                    private val debugProcessHandler: ProcessHandler,
                                    private val consoleView: ConsoleView?) : ProcessDebugger {

    protected val executedCommands: BlockingDeque<AbstractCommand<out ParseResult?>> = LinkedBlockingDeque()
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

    final override fun oldestExecutedCommand(): AbstractCommand<out ParseResult?>? = executedCommands.peekFirst()

    final override fun removeOldestExecutedCommand() { executedCommands.pollFirst() }

    protected open fun doClose() {}

    protected fun enqueueCommandWithPriority(command: AbstractCommand<*>): Unit = queue.addCommand(command, true)

    override fun setReadyForInput(): Unit = queue.setReadyForInput()

    /**
     * Executes command immediately
     */
    protected fun execute(command: AbstractCommand<out ParseResult?>) {
        val text = command.getText()
        writeLock.lock()
        executedCommands.addLast(command)
        command.callback?.execBeforeSending()
        printCommandIfNeeded(text)
        sendCommandToProcess(text)
        if (executedCommands.peekLast() is TraceCommand) {
            debugStarted = true
        }
        writeLock.unlock()
    }

    private fun printCommandIfNeeded(text: String) {
        if (executedCommands.peekLast() !is HiddenCommand) {
            print(text)
            consoleView?.print(text, ConsoleViewContentType.SYSTEM_OUTPUT)
        }
    }

    private fun sendCommandToProcess(text: String) {
        val os = debugProcessHandler.getProcessInput()!!
        os.write(text.toByteArray())
        os.flush()
    }
}