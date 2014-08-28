package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.junit.Test
import org.junit.Before
import org.junit.After
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import java.util.Properties
import java.io.FileInputStream
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.HistoryResult
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointDescription
import org.jetbrains.haskell.debugger.parser.LocalBinding
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XValue
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.protocol.CommandCallback

public abstract class DebuggerTest<T : ProcessDebugger> {

    class object {

        public val PROPERTIES_FILE: String = "unittest.properties"
        public val TEST_MODULE_FILE: String = "TestMain.hs"

        public val MAIN_LINE: Int = 10
        public val QSORT_LINE: Int = 6

        public var properties: Properties? = null;

        {
            properties = Properties()
            val filePath = javaClass.getResource("/$PROPERTIES_FILE")?.getFile()
            assertNotNull(filePath)
            properties!!.load(FileInputStream(filePath!!))
        }

        public enum class Result {
            TRACE_FINISHED
            POSITION_REACHED
            BREAKPOINT_REACHED
            EXCEPTION_REACHED
            BREAKPOINT_REMOVED
            BREAKPOINT_SET_AT
        }

        public class BreakpointPosition(val module: String, val line: Int) {
            override fun equals(other: Any?): Boolean {
                if (other == null || other !is BreakpointPosition) {
                    return false
                }
                return module.equals(other.module) && line.equals(other.line)
            }

            override fun hashCode(): Int = module.hashCode() * 31 + line
        }

        public class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: HaskellLineBreakpointDescription)

    }

    public inner class TestRespondent : DebugRespondent {
        public val moduleName: String = "Main"
        public var result: Result? = null
        public var context: HsSuspendContext? = null
        public var breakpoint: HaskellLineBreakpointDescription? = null
        public val breakpoints: MutableMap<BreakpointPosition, BreakpointEntry> = hashMapOf()
        public var currentFrame: HsHistoryFrame? = null
        public var history: HistoryResult? = null

        override fun traceFinished() = withSignal {
            result = Result.TRACE_FINISHED
        }

        override fun positionReached(context: HsSuspendContext) = withSignal {
            result = Result.POSITION_REACHED
            this.context = context
        }

        override fun breakpointReached(breakpoint: HaskellLineBreakpointDescription, context: HsSuspendContext) = withSignal {
            result = Result.BREAKPOINT_REACHED
            this.breakpoint = breakpoint
            this.context = context
        }

        override fun exceptionReached(context: HsSuspendContext) = withSignal {
            result = Result.EXCEPTION_REACHED
            this.context = context
        }

        override fun breakpointRemoved() = withSignal {
            result = Result.BREAKPOINT_REMOVED
        }

        override fun getBreakpointAt(module: String, line: Int): HaskellLineBreakpointDescription? {
            assert(module.equals(moduleName))
            return breakpoints.get(BreakpointPosition(module, line))?.breakpoint
        }

        override fun setBreakpointNumberAt(breakpointNumber: Int, module: String, line: Int) = withSignal {
            assert(module.equals(moduleName))
            result = Result.BREAKPOINT_SET_AT
            val entry = breakpoints.get(BreakpointPosition(module, line))
            if (entry != null) {
                entry.breakpointNumber = breakpointNumber
            }
        }

        override fun resetHistoryStack() {
            currentFrame = null
            history = null
        }

        override fun historyChange(currentFrame: HsHistoryFrame, history: HistoryResult?) {
            this.currentFrame = currentFrame
            this.history = history
        }

        override fun getModuleByFile(filename: String): String = moduleName

        public fun addBreakpointToMap(module: String, line: Int) {
            assert(module.equals(moduleName))
            val position = BreakpointPosition(module, line)
            val breakpoint = HaskellLineBreakpointDescription(module, line, null)
            val entry = BreakpointEntry(null, breakpoint)
            breakpoints.put(position, entry)
        }
    }

    private fun withSignal(method: () -> Unit) {
        syncObject.lock()
        try {
            method()
        } finally {
            syncObject.signal()
            syncObject.unlock()
        }
    }

    private fun withAwait(method: () -> Unit) {
        syncObject.lock()
        try {
            method()
        } finally {
            syncObject.await()
            syncObject.unlock()
        }
    }

    protected abstract fun createDebugger(file: File, respondent: DebugRespondent): T
    protected abstract fun stopDebuggerServices()

    private var syncObject: SyncObject = SyncObject()
    private var debugger: T? = null
    private var respondent: TestRespondent? = null

    private fun assertResult(expected: Result) = assertEquals(expected, respondent?.result)

    Before public fun setupDebugger() {
        val url = this.javaClass.getResource("/${TEST_MODULE_FILE}")
        val file = url?.getFile()
        assertNotNull(file)
        val testFile = File(file!!)
        assertTrue(testFile.exists())

        syncObject = SyncObject()
        respondent = TestRespondent()

        debugger = createDebugger(testFile, respondent!!)

        debugger?.prepareDebugger()
    }

    After public fun stopDebugger() {
        debugger?.close()
        stopDebuggerServices()
    }

    Test public fun mainTraceTest() {
        withAwait { debugger!!.trace(null) }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun customTraceTest() {
        withAwait { debugger!!.trace("print $ qsort [5, 1, 2]") }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun setBreakpointTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", QSORT_LINE)
            debugger!!.setBreakpoint("Main", QSORT_LINE)
        }
        assertResult(Result.BREAKPOINT_SET_AT)
    }

    Test public fun stoppedAtBreakpointTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", MAIN_LINE)
            debugger!!.setBreakpoint("Main", MAIN_LINE)
        }
        withAwait { debugger!!.trace(null) }
        assertResult(Result.BREAKPOINT_REACHED)
        val filePosition = respondent!!.context?.threadInfo?.topFrameInfo?.filePosition
        assertEquals(respondent!!.breakpoints.get(BreakpointPosition("Main", MAIN_LINE))!!.breakpoint, respondent!!.breakpoint)
        assertEquals(MAIN_LINE, filePosition?.rawStartLine)
        assertEquals(MAIN_LINE, filePosition?.rawEndLine)
        assertEquals(8, filePosition?.rawStartSymbol)
        assertEquals(57, filePosition?.rawEndSymbol)
    }

    Test public fun resumeTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", MAIN_LINE)
            debugger!!.setBreakpoint("Main", MAIN_LINE)
        }
        withAwait { debugger!!.trace(null) }
        withAwait { debugger!!.resume() }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun removeBreakpointTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", QSORT_LINE)
            debugger!!.setBreakpoint("Main", QSORT_LINE)
        }
        withAwait { debugger!!.trace(null) }
        withAwait { debugger!!.removeBreakpoint("Main", respondent!!.breakpoints.get(BreakpointPosition("Main", 6))!!.breakpointNumber!!) }
        assertResult(Result.BREAKPOINT_REMOVED)
        withAwait { debugger!!.resume() }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun stepTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", MAIN_LINE)
            debugger!!.setBreakpoint("Main", MAIN_LINE)
        }
        withAwait { debugger!!.trace(null) }
        withAwait { debugger!!.stepInto() }
        assertResult(Result.POSITION_REACHED)
        val filePosition = respondent!!.context?.threadInfo?.topFrameInfo?.filePosition
        assertEquals(MAIN_LINE, filePosition?.rawStartLine)
        assertEquals(MAIN_LINE, filePosition?.rawEndLine)
        assertEquals(16, filePosition?.rawStartSymbol)
        assertEquals(57, filePosition?.rawEndSymbol)
    }

    Test public fun stepLocalTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", 13)
            debugger!!.setBreakpoint("Main", 13)
        }
        assertResult(Result.BREAKPOINT_SET_AT)
        withAwait { debugger!!.trace("steplocaltest") }
        assertResult(Result.BREAKPOINT_REACHED)
        withAwait { debugger!!.stepOver() }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun runToPositionTest() {
        withAwait { debugger!!.runToPosition("Main", MAIN_LINE) }
        assertResult(Result.POSITION_REACHED)
        val filePosition = respondent!!.context?.threadInfo?.topFrameInfo?.filePosition
        assertEquals(MAIN_LINE, filePosition?.rawStartLine)
        assertEquals(MAIN_LINE, filePosition?.rawEndLine)
        assertEquals(8, filePosition?.rawStartSymbol)
        assertEquals(57, filePosition?.rawEndSymbol)
    }

    Test public fun uncaughtExceptionBreakpointTest1() {
        debugger!!.setExceptionBreakpoint(true)
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.EXCEPTION_REACHED)
    }

    Test public fun uncaughtExceptionBreakpointTest2() {
        debugger!!.setExceptionBreakpoint(true)
        withAwait { debugger!!.trace("caughtMain") }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun anyExceptionBreakpointTest1() {
        debugger!!.setExceptionBreakpoint(false)
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.EXCEPTION_REACHED)
    }

    Test public fun anyExceptionBreakpointTest2() {
        debugger!!.setExceptionBreakpoint(false)
        withAwait { debugger!!.trace("caughtMain") }
        assertResult(Result.EXCEPTION_REACHED)
    }

    Test public fun removeExceptionBreakpointTest1() {
        debugger!!.setExceptionBreakpoint(false)
        debugger!!.removeExceptionBreakpoint()
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun removeExceptionBreakpointTest2() {
        debugger!!.setExceptionBreakpoint(true)
        debugger!!.removeExceptionBreakpoint()
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.TRACE_FINISHED)
    }

    Test public fun evaluateTest1() {
        var evalResult: XValue? = null
        withAwait {
            debugger!!.evaluateExpression("expression", object : XDebuggerEvaluator.XEvaluationCallback {
                override fun evaluated(result: XValue) = withSignal { evalResult = result }
                override fun errorOccurred(errorMessage: String) = withSignal { evalResult = null }
            })
        }
        assertTrue(evalResult is HsDebugValue)
        with (evalResult as HsDebugValue) {
            assertEquals("Int", binding.typeName)
            assertEquals("_", binding.value)
        }
    }

    Test public fun evaluateTest2() {
        var evalResult: XValue? = null
        withAwait {
            debugger!!.evaluateExpression("(1 + 2 * 3) :: Int", object : XDebuggerEvaluator.XEvaluationCallback {
                override fun evaluated(result: XValue) = withSignal { evalResult = result }
                override fun errorOccurred(errorMessage: String) = withSignal { evalResult = null }
            })
        }
        assertTrue(evalResult is HsDebugValue)
        with (evalResult as HsDebugValue) {
            assertEquals("Int", binding.typeName)
            assertEquals("_", binding.value)
        }
    }

    Test public fun forceTest() {
        var forceResult: LocalBinding? = null
        withAwait { debugger!!.setBreakpoint("Main", QSORT_LINE) }
        withAwait { debugger!!.trace(null) }
        withAwait {
            debugger!!.force("left", object : CommandCallback<LocalBinding?>() {
                override fun execAfterParsing(result: LocalBinding?) = withSignal { forceResult = result }
            })
        }
        assertEquals("[4,2,3]", forceResult?.value)
    }
}