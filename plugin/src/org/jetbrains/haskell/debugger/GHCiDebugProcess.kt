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
import com.intellij.execution.process.ProcessOutputTypes

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              val executionConsole: ExecutionConsole,
                              val myProcessHandler: ProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    private val debugger: GHCiDebugger
    private val inputReadinessListener: InputReadinessListener

    public val readyForInput: AtomicBoolean = AtomicBoolean(false)

    public val debugFinished: Boolean = false;

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this)

        myProcessHandler.addProcessListener(this)

        inputReadinessListener = InputReadinessListener(this)
        inputReadinessListener.start()
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
        debugger.prepareGHCi()
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
        if (outputType == ProcessOutputTypes.STDOUT) {
            print(event?.getText())
            handleGHCiOutput(event?.getText())
        } else if (outputType == ProcessOutputTypes.STDERR) {
            val text = fixStderrOutput(event?.getText())
            print(text)
        }
        if (!inputReadinessListener.connected && isReadyForInput(event?.getText())) {
            readyForInput.set(true)
        }
    }

    private fun isReadyForInput(line: String?): Boolean =
            line?.endsWith(PROMPT_LINE) ?: false

    // methods to handle GHCi output
    private fun handleGHCiOutput(output: String?) {
        /*
         * todo:
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
//            tryHandleDebugFinished(output)
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
                if (callStack == null) { // or when new history invocation
                    callStack = ArrayList<CallInfo>()
                }
                callStack!!.add(CallInfo(index, function, filePosition))
            } else {
                throw RuntimeException("Wrong GHCi output occured while handling HistoryCommand result")
            }
        }
    }

//    private fun tryHandleDebugFinished(output: String) {
//        // temporary
//        if (debugger.debugStarted && output.equals("*Main> ")) {
//            getSession()?.stop()
//        }
//    }

    private fun fixStderrOutput(text: String?): String? {
        return text?.replace("" + 0.toChar(), "")?.replace("" + 1.toChar(), "")
    }


    /*
     * Maybe better to move class outside
     */

    public class FilePosition private (val file: String, val startLine: Int, val startSymbol: Int,
                                       val endLine: Int, val endSymbol: Int) {

        class object {
            public fun tryCreateFilePosition(line: String): FilePosition? {
                for (i in 0..(FILE_POSITION_PATTERNS.size - 1)) {
                    val matcher = Pattern.compile(FILE_POSITION_PATTERNS[i]).matcher(line)
                    if (matcher.matches()) {
                        val path = matcher.toMatchResult().group(1)!!
                        if (!File(path).exists()) {
                            return null;
                        }
                        val values = IntArray(matcher.groupCount() - 1)
                        for (j in 0..(values.size - 1)) {
                            values[j] = Integer.parseInt(matcher.toMatchResult().group(j + 2)!!)
                        }
                        return FilePosition(path, values[POSITION_PATTERN_PLACES[i][0]], values[POSITION_PATTERN_PLACES[i][1]],
                                values[POSITION_PATTERN_PLACES[i][2]], values[POSITION_PATTERN_PLACES[i][3]])
                    }
                }
                return null;
            }

            private val FILE_POSITION_PATTERNS = array(
                    "(.*):(\\d+):(\\d+)",
                    "(.*):(\\d+):(\\d+)-(\\d+)",
                    "(.*):\\((\\d+),(\\d+)\\)-\\((\\d+),(\\d+)\\)"
            )

            private val POSITION_PATTERN_PLACES = array(
                    array(0, 1, 0, 1),
                    array(0, 1, 0, 2),
                    array(0, 1, 2, 3)
            )
        }
    }

    class object {

        private val CALL_INFO_PATTERN = "-(\\d+)\\s+:\\s(.*)\\s\\((.*)\\)"
        private val STOPPED_AT_PATTERN = "Stopped\\sat\\s(.*)"

        public val PROMPT_LINE: String = "debug> "

        // todo: change
        public val INPUT_READINESS_PORT: Int = 12345
    }
}