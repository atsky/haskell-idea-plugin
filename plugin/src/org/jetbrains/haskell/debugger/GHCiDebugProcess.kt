package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.XSourcePosition
import com.intellij.execution.process.ProcessHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.execution.process.ProcessListener
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import org.jetbrains.haskell.debugger.commands.SetBreakpointCommand
import org.jetbrains.haskell.debugger.commands.TraceCommand
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.commands.StepIntoCommand
import org.jetbrains.haskell.debugger.commands.StepOverCommand
import org.jetbrains.haskell.debugger.commands.ResumeCommand
import java.io.File
import java.util.regex.Pattern
import java.util.ArrayList
import org.codehaus.groovy.tools.shell.commands.HistoryCommand

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              val executionConsole: ExecutionConsole,
                              val myProcessHandler: ProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    private val debugger: GHCiDebugger

    public val readyForInput: AtomicBoolean = AtomicBoolean(false);

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this)

        myProcessHandler.addProcessListener(this)
    }

    private val _breakpointHandlers: Array<XBreakpointHandler<*>> = array(
            HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this)
    )

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> {
        return _breakpointHandlers
    }

    private class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>)
    private val registeredBreakpoints: MutableMap<Int, BreakpointEntry> = hashMapOf()

    //    private fun tryAddBreakpointHandlersFromExtensions() {
    //        val extPointName: ExtensionPointName<HaskellBreakpointHandlerFactory>? = HaskellBreakpointHandlerFactory.EXTENSION_POINT_NAME
    //        if(extPointName != null) {
    //            for (factory in Extensions.getExtensions(extPointName)) {
    //                _breakpointHandlers.add(factory.createBreakpointHandler(this))
    //            }
    //        }
    //    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return debuggerEditorsProvider
    }

    override fun doGetProcessHandler(): ProcessHandler? {
        return myProcessHandler
    }

    override fun createConsole(): ExecutionConsole {
        return executionConsole
    }

    override fun startStepOver() {
        debugger.stepOver()
    }

    override fun startStepInto() {
        debugger.stepInto()
    }

    override fun startStepOut() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        debugger.close();
    }

    override fun resume() {
        debugger.resume()
    }

    override fun runToPosition(position: XSourcePosition) {
        throw UnsupportedOperationException()
    }

    public fun addBreakpoint(position: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        registeredBreakpoints.put(position, BreakpointEntry(null, breakpoint))
        debugger.setBreakpoint(position)
    }

    public fun removeBreakpoint(position: Int) {
        val breakpointNumber: Int? = registeredBreakpoints.get(position)?.breakpointNumber
        if (breakpointNumber != null) {
            registeredBreakpoints.remove(position)
            debugger.removeBreakpoint(breakpointNumber)
        }
    }

    override fun sessionInitialized() {
        super<XDebugProcess>.sessionInitialized()
        debugger.trace()
    }


    public fun printToConsole(text: String) {
        (executionConsole as ConsoleView).print(text, ConsoleViewContentType.NORMAL_OUTPUT)
    }


    // ProcessListener

    private class CallInfo(val index: Int, val function: String, val position: FilePosition)
    private var callStack: ArrayList<CallInfo>? = null

    override fun startNotified(event: ProcessEvent?) {
    }

    override fun processTerminated(event: ProcessEvent?) {
    }

    override fun processWillTerminate(event: ProcessEvent?, willBeDestroyed: Boolean) {
    }

    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        print(event?.getText())
        handleGHCiOutput(event?.getText())

        if (isReadyForInput(event?.getText())) {
            readyForInput.set(true)
        }
    }

    private fun isReadyForInput(line: String?): Boolean = line?.endsWith("*Main> ") ?: false    //temporary

    // methods to handle GHCi output
    private fun handleGHCiOutput(output: String?) {
        /*
         * todo:
         * "handle" methods do not work when there was an output without '\n' at the end of it before "Stopped at".
         * Need to find the way to distinguish debug output and program output.
         * Debug output is always at the end before input is available and fits some patterns, need to use it.
         */
        if (output != null) {
            when (debugger.lastCommand) {
                is SetBreakpointCommand -> handleSetBreakpointCommandResult(output)
                is TraceCommand,
                is ResumeCommand -> tryHandleStoppedAtBreakpoint(output)
                is StepIntoCommand,
                is StepOverCommand -> tryHandleStoppedAtPosition(output)
                is HistoryCommand -> handleHistory(output)
            }
            tryHandleDebugFinished(output)
        }
    }

    private fun handleSetBreakpointCommandResult(output: String) {
        //temporary and not optimal, later parser should do this work (added just for testing)
        val parts = output.split(' ')

        if (parts.size > 4 && parts[0] == "Breakpoint" && parts[2] == "activated" && parts[3] == "at") {
            val breakpointNumber = parts[1].toInt()
            val lastWord = parts[parts.size - 1]
            val lineNumberBegSubstr = lastWord.substring(lastWord.indexOf(':') + 1)
            val lineNumber = lineNumberBegSubstr.substring(0, lineNumberBegSubstr.indexOf(':')).toInt()
            val entry = registeredBreakpoints.get(lineNumber)
            if (entry != null) {
                entry.breakpointNumber = breakpointNumber
            }
            debugger.lastCommand = null
        } else {
            throw RuntimeException("Wrong GHCi output occured while handling SetBreakpointCommand result")
        }
    }

    private fun tryHandleStoppedAtBreakpoint(output: String) {
        val matcher = Pattern.compile("(.*)" + STOPPED_AT_PATTERN).matcher(output.trim())
        if (matcher.matches()) {
            val str = matcher.toMatchResult().group(2)!!
            val filePosition = FilePosition.tryCreateFilePosition(str)
            if (filePosition != null) {
                val lineNumber = filePosition.startLine
                val breakpoint = registeredBreakpoints.get(lineNumber)!!.breakpoint
                val context = object : XSuspendContext() {}
                getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
            }
        }
    }


    private fun tryHandleStoppedAtPosition(output: String) {
        val matcher = Pattern.compile("(.*)" + STOPPED_AT_PATTERN).matcher(output.trim())
        if (matcher.matches()) {
            val filePosition = FilePosition.tryCreateFilePosition(matcher.toMatchResult().group(2)!!)
            if (filePosition != null) {
                val context = object : XSuspendContext() {}
                getSession()!!.positionReached(context)
            }
        }
    }

    private fun handleHistory(output: String) {
        if (output.trim().equals("<end of history>")) {
            // mark history as ready
        } else {
            val matcher = Pattern.compile(CALL_INFO_PATTERN).matcher(output.trim())
            if (matcher.matches()) {
                val index = -Integer.parseInt(matcher.toMatchResult().group(1)!!)
                val function = matcher.toMatchResult().group(2)!!
                val line = matcher.toMatchResult().group(3)!!
                val filePosition = FilePosition.tryCreateFilePosition(line)
                if (filePosition == null) {
                    throw RuntimeException("Wrong GHCi output occured while handling HistoryCommand result")
                }
                if (callStack == null) {
                    callStack = ArrayList<CallInfo>()
                }
                callStack!!.add(CallInfo(index, function, filePosition))
            } else {
                throw RuntimeException("Wrong GHCi output occured while handling HistoryCommand result")
            }
        }
    }

    private fun tryHandleDebugFinished(output: String) {
        // temporary
        if (debugger.debugStarted && output.equals("*Main> ")) {
            getSession()?.stop()
        }
    }


    /*
     * Maybe better to move class outside
     */

    public class FilePosition private (val file: String, val startLine: Int, val startSymbol: Int,
                                       val endLine: Int, val endSymbol: Int) {

        class object {
            public fun tryCreateFilePosition(line: String): FilePosition? {
                val matcher0 = Pattern.compile(FILE_POSITION_PATTERN_0).matcher(line)
                val matcher1 = Pattern.compile(FILE_POSITION_PATTERN_1).matcher(line)
                val matcher2 = Pattern.compile(FILE_POSITION_PATTERN_2).matcher(line)
                if (matcher0.matches()) {
                    val path = matcher0.toMatchResult().group(1)!!
                    if (!File(path).exists()) {
                        return null;
                    }
                    val values = IntArray(2)
                    for (i in 0..(values.size - 1)) {
                        values[i] = Integer.parseInt(matcher0.toMatchResult().group(i + 2)!!)
                    }
                    return FilePosition(path, values[0], values[1], values[0], values[1])
                } else if (matcher1.matches()) {
                    val path = matcher1.toMatchResult().group(1)!!
                    if (!File(path).exists()) {
                        return null;
                    }
                    val values = IntArray(3)
                    for (i in 0..(values.size - 1)) {
                        values[i] = Integer.parseInt(matcher1.toMatchResult().group(i + 2)!!)
                    }
                    return FilePosition(path, values[0], values[1], values[0], values[2])
                } else if (matcher2.matches()) {
                    val path = matcher2.toMatchResult().group(1)!!
                    if (!File(path).exists()) {
                        return null;
                    }
                    val values = IntArray(4)
                    for (i in 0..(values.size - 1)) {
                        values[i] = Integer.parseInt(matcher2.toMatchResult().group(i + 2)!!)
                    }
                    return FilePosition(path, values[0], values[1], values[2], values[3])
                } else {
                    return null;
                }
            }

            private val FILE_POSITION_PATTERN_0 = "(.*):(\\d+):(\\d+)"
            private val FILE_POSITION_PATTERN_1 = "(.*):(\\d+):(\\d+)-(\\d+)"
            private val FILE_POSITION_PATTERN_2 = "(.*):\\((\\d+),(\\d+)\\)-\\((\\d+),(\\d+)\\)"
        }
    }

    class object {

        private val CALL_INFO_PATTERN = "-(\\d+)\\s+:\\s(.*)\\s\\((.*)\\)"
        private val STOPPED_AT_PATTERN = "Stopped\\sat\\s(.*)"

    }
}