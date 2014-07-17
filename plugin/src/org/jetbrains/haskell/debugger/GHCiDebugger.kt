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

/**
 * Created by vlad on 7/11/14.
 */

public class GHCiDebugger(val debugProcess: GHCiDebugProcess) : ProcessDebugger {

    private val lockObject = Any()
    private val queue: CommandQueue

    private val handleName = "handle"

    public var lastCommand: AbstractCommand? = null;

    {
        queue = CommandQueue(this, debugProcess.readyForInput)
        queue.start()
    }
    public var debugStarted: Boolean = false
        private set

    override fun trace() {
        queue.addCommand(TraceCommand("main >> (withSocketsDo $ $handleName >>= \\ h -> hPutChar h (chr 1) >> hClose h)"))
    }

    override fun execute(command: AbstractCommand) {
        val bytes = command.getBytes()

        synchronized(lockObject) {
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

    override fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?) {
        queue.addCommand(HistoryCommand(breakpoint))
    }

    override fun prepareGHCi() {
        execute(object : HiddenCommand() {
            override fun getBytes(): ByteArray {
                return (":set prompt \"" + GHCiDebugProcess.PROMPT_LINE + "\"\n").toByteArray()
            }
        })

        val connectTo_host_port = "\\host port_ -> let port = toEnum port_ in " +
                "socket AF_INET Stream 0 >>= " +
                "(\\sock -> liftM hostAddresses (getHostByName host) >>= " +
                "(\\addrs -> connect sock (SockAddrInet port (head addrs)) >> " +
                "socketToHandle sock ReadWriteMode >>=  " +
                "(\\handle -> return handle)))"
        val host = "\"localhost\""
        val port = GHCiDebugProcess.INPUT_READINESS_PORT
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
            queue.addCommand(object : HiddenCommand() {
                override fun getBytes(): ByteArray {
                    return cmd.toByteArray()
                }
            })
        }
    }

    override fun close() {
        queue.stop()
    }
}