package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.RemoveBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.StepIntoCommand
import org.jetbrains.haskell.debugger.protocol.StepOverCommand
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.HistoryCommand
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.protocol.RealTimeCommand
import java.util.Deque
import java.util.LinkedList
import com.intellij.openapi.util.Key
import com.intellij.execution.process.ProcessOutputTypes
import java.util.concurrent.atomic.AtomicBoolean
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.frames.HsCommonStackFrame
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.protocol.SequenceOfBacksCommand
import org.jetbrains.haskell.debugger.protocol.SequenceOfForwardsCommand

/**
 * Created by vlad on 7/11/14.
 */

public class GHCiDebugger(val debugProcess: HaskellDebugProcess) : ProcessDebugger {

    class object {
        public val PROMPT_LINE: String = "debug> "
    }

    private val inputReadinessChecker: InputReadinessChecker
    private var collectedOutput: StringBuilder = StringBuilder()
    private val queue: CommandQueue
    private val writeLock = Any()
    private val handleName = "handle"

    public val processStopped: AtomicBoolean = AtomicBoolean(false)

    public var lastCommand: AbstractCommand? = null;

    {
        queue = CommandQueue({(command : AbstractCommand) -> execute(command)})
        queue.start()

        inputReadinessChecker = InputReadinessChecker(this, {() -> onStopSignal()})
        inputReadinessChecker.start()
    }
    public var debugStarted: Boolean = false
        private set

    override fun trace() {
        queue.addCommand(TraceCommand("main >> (withSocketsDo $ $handleName >>= \\ h -> hPutChar h (chr 1) >> hClose h)"))
    }

    /**
     * Executes command immediately
     */
    private fun execute(command: AbstractCommand) {
        val bytes = command.getBytes()

        synchronized(writeLock) {
            lastCommand = command

            if (lastCommand !is HiddenCommand) {
                debugProcess.printToConsole(String(bytes))

                System.out.write(bytes)
                System.out.flush()
            }

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()

            if (lastCommand is TraceCommand) {
                debugStarted = true
            }
        }
    }

    override fun setBreakpoint(line: Int) = queue.addCommand(SetBreakpointCommand(line))

    override fun removeBreakpoint(breakpointNumber: Int) = queue.addCommand(RemoveBreakpointCommand(breakpointNumber))

    override fun stepInto() {
        queue.addCommand(StepIntoCommand())
    }

    override fun stepOver() {
        queue.addCommand(StepOverCommand())
    }

    override fun resume() {
        queue.addCommand(ResumeCommand())
    }

    override fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?, topFrameInfo : HsTopStackFrameInfo) {
        queue.addCommand(HistoryCommand(breakpoint, topFrameInfo))
    }

    override public fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand) {
        queue.addCommand(sequenceOfBacksCommand)
    }
    override public fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand) {
        queue.addCommand(sequenceOfForwardsCommand)
    }

    override fun requestVariables() {
        throw UnsupportedOperationException()
    }

    override fun prepareGHCi() {
        execute(HiddenCommand.createInstance(":set prompt \"$PROMPT_LINE\"\n"))

        val connectTo_host_port = "\\host port_ -> let port = toEnum port_ in " +
                "socket AF_INET Stream 0 >>= " +
                "(\\sock -> liftM hostAddresses (getHostByName host) >>= " +
                "(\\addrs -> connect sock (SockAddrInet port (head addrs)) >> " +
                "socketToHandle sock ReadWriteMode >>=  " +
                "(\\handle -> return handle)))"
        val host = "\"localhost\""
        val port = HaskellDebugProcess.INPUT_READINESS_PORT
        var stop_cmd = "withSocketsDo $ $handleName >>= \\ h -> hPutChar h (chr 0) >> hClose h"

        /*
         * todo:
         * 1. need to be careful with concurrency of modules
         * 2. handle name can be used
         */
        val commands = array(
                ":m +System.IO\n",
                ":m +Data.Char\n",
                ":m +Network.Socket\n",
                ":m +Network.BSD\n",
                ":m +Control.Monad\n",
                ":m +Control.Concurrent\n",
                "let $handleName = ($connectTo_host_port) $host $port\n",
                ":set stop $stop_cmd\n"
        )
        for (cmd in commands) {
            queue.addCommand(HiddenCommand.createInstance(cmd))
        }
    }

    override fun close() {
        inputReadinessChecker.stop()
        queue.stop()
    }

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        if (outputType != ProcessOutputTypes.SYSTEM) {
            collectedOutput.append(text)
            if (simpleReadinessCheck() &&
                    (processStopped.get() || !inputReadinessChecker.connected || outputIsDefinite())) {
                handleOutput()
                processStopped.set(false)
                setReadyForInput()
            }
        }
    }

    private fun setReadyForInput() {
        queue.setReadyForInput()
    }

    private fun handleOutput() {
        lastCommand?.handleOutput(collectedOutput.toString().split('\n').toLinkedList(), debugProcess)
        collectedOutput = StringBuilder()
    }

    private fun outputIsDefinite(): Boolean {
        return lastCommand is RealTimeCommand
    }

    private fun simpleReadinessCheck(): Boolean = collectedOutput.toString().endsWith(PROMPT_LINE)

    private fun onStopSignal() {
        debugProcess.getSession()?.stop()
    }
}