package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.junit.Test
import org.junit.Before
import org.junit.After
import java.io.File
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
import org.junit.Assert

abstract class DebuggerTest<T : ProcessDebugger> {

    companion object {

        val PROPERTIES_FILE: String = "unittest.properties"
        val TEST_MODULE_FILE: String = "TestMain.hs"

        val MAIN_LINE: Int = 10
        val QSORT_LINE: Int = 6

        var properties: Properties? = null

        init {
            properties = Properties()
            val filePath = javaClass.getResource("/$PROPERTIES_FILE")?.file
            Assert.assertNotNull(filePath)
            properties!!.load(FileInputStream(filePath!!))
        }

        enum class Result {
            TRACE_FINISHED,
            POSITION_REACHED,
            BREAKPOINT_REACHED,
            EXCEPTION_REACHED,
            BREAKPOINT_REMOVED,
            BREAKPOINT_SET_AT
        }

        class BreakpointPosition(val module: String, val line: Int) {
            override fun equals(other: Any?): Boolean {
                if (other == null || other !is BreakpointPosition) {
                    return false
                }
                return module.equals(other.module) && line.equals(other.line)
            }

            override fun hashCode(): Int = module.hashCode() * 31 + line
        }

        class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: HaskellLineBreakpointDescription)

    }

    inner class TestRespondent : DebugRespondent {
        val moduleName: String = "Main"
        var result: Result? = null
        var context: HsSuspendContext? = null
        var breakpoint: HaskellLineBreakpointDescription? = null
        val breakpoints: MutableMap<BreakpointPosition, BreakpointEntry> = hashMapOf()
        var currentFrame: HsHistoryFrame? = null
        var history: HistoryResult? = null

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

        fun addBreakpointToMap(module: String, line: Int) {
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

    private fun assertResult(expected: Result) = Assert.assertEquals(expected, respondent?.result)

    @Before fun setupDebugger() {
        val url = this.javaClass.getResource("/${TEST_MODULE_FILE}")
        val file = url?.file
        Assert.assertNotNull(file)
        val testFile = File(file!!)
        Assert.assertTrue(testFile.exists())

        syncObject = SyncObject()
        respondent = TestRespondent()

        debugger = createDebugger(testFile, respondent!!)

        debugger?.prepareDebugger()
    }

    @After fun stopDebugger() {
        debugger?.close()
        stopDebuggerServices()
    }

    @Test fun mainTraceTest() {
        withAwait { debugger!!.trace(null) }
        assertResult(Result.TRACE_FINISHED)
    }

    @Test fun customTraceTest() {
        withAwait { debugger!!.trace("print $ qsort [5, 1, 2]") }
        assertResult(Result.TRACE_FINISHED)
    }

    @Test fun setBreakpointTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", QSORT_LINE)
            debugger!!.setBreakpoint("Main", QSORT_LINE)
        }
        assertResult(Result.BREAKPOINT_SET_AT)
    }

    @Test fun stoppedAtBreakpointTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", MAIN_LINE)
            debugger!!.setBreakpoint("Main", MAIN_LINE)
        }
        withAwait { debugger!!.trace(null) }
        assertResult(Result.BREAKPOINT_REACHED)
        val filePosition = respondent!!.context?.threadInfo?.topFrameInfo?.filePosition
        Assert.assertEquals(respondent!!.breakpoints.get(BreakpointPosition("Main", MAIN_LINE))!!.breakpoint, respondent!!.breakpoint)
        Assert.assertEquals(MAIN_LINE, filePosition?.rawStartLine)
        Assert.assertEquals(MAIN_LINE, filePosition?.rawEndLine)
        Assert.assertEquals(8, filePosition?.rawStartSymbol)
        Assert.assertEquals(57, filePosition?.rawEndSymbol)
    }

    @Test fun resumeTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", MAIN_LINE)
            debugger!!.setBreakpoint("Main", MAIN_LINE)
        }
        withAwait { debugger!!.trace(null) }
        withAwait { debugger!!.resume() }
        assertResult(Result.TRACE_FINISHED)
    }

    @Test fun removeBreakpointTest() {
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

    @Test fun stepTest() {
        withAwait {
            respondent!!.addBreakpointToMap("Main", MAIN_LINE)
            debugger!!.setBreakpoint("Main", MAIN_LINE)
        }
        withAwait { debugger!!.trace(null) }
        withAwait { debugger!!.stepInto() }
        assertResult(Result.POSITION_REACHED)
        val filePosition = respondent!!.context?.threadInfo?.topFrameInfo?.filePosition
        Assert.assertEquals(MAIN_LINE, filePosition?.rawStartLine)
        Assert.assertEquals(MAIN_LINE, filePosition?.rawEndLine)
        Assert.assertEquals(16, filePosition?.rawStartSymbol)
        Assert.assertEquals(57, filePosition?.rawEndSymbol)
    }

    @Test fun stepLocalTest() {
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

    @Test fun runToPositionTest() {
        withAwait { debugger!!.runToPosition("Main", MAIN_LINE) }
        assertResult(Result.POSITION_REACHED)
        val filePosition = respondent!!.context?.threadInfo?.topFrameInfo?.filePosition
        Assert.assertEquals(MAIN_LINE, filePosition?.rawStartLine)
        Assert.assertEquals(MAIN_LINE, filePosition?.rawEndLine)
        Assert.assertEquals(8, filePosition?.rawStartSymbol)
        Assert.assertEquals(57, filePosition?.rawEndSymbol)
    }

    @Test fun uncaughtExceptionBreakpointTest1() {
        debugger!!.setExceptionBreakpoint(true)
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.EXCEPTION_REACHED)
    }

    @Test fun uncaughtExceptionBreakpointTest2() {
        debugger!!.setExceptionBreakpoint(true)
        withAwait { debugger!!.trace("caughtMain") }
        assertResult(Result.TRACE_FINISHED)
    }

    @Test fun anyExceptionBreakpointTest1() {
        debugger!!.setExceptionBreakpoint(false)
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.EXCEPTION_REACHED)
    }

    @Test fun anyExceptionBreakpointTest2() {
        debugger!!.setExceptionBreakpoint(false)
        withAwait { debugger!!.trace("caughtMain") }
        assertResult(Result.EXCEPTION_REACHED)
    }

    @Test fun removeExceptionBreakpointTest1() {
        debugger!!.setExceptionBreakpoint(false)
        debugger!!.removeExceptionBreakpoint()
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.TRACE_FINISHED)
    }

    @Test fun removeExceptionBreakpointTest2() {
        debugger!!.setExceptionBreakpoint(true)
        debugger!!.removeExceptionBreakpoint()
        withAwait { debugger!!.trace("uncaughtMain") }
        assertResult(Result.TRACE_FINISHED)
    }

    @Test fun evaluateTest1() {
        var evalResult: XValue? = null
        withAwait {
            debugger!!.evaluateExpression("expression", object : XDebuggerEvaluator.XEvaluationCallback {
                override fun evaluated(result: XValue) = withSignal { evalResult = result }
                override fun errorOccurred(errorMessage: String) = withSignal { evalResult = null }
            })
        }
        Assert.assertTrue(evalResult is HsDebugValue)
        with (evalResult as HsDebugValue) {
            Assert.assertEquals("Int", binding.typeName)
            Assert.assertEquals("_", binding.value)
        }
    }

    @Test fun evaluateTest2() {
        var evalResult: XValue? = null
        withAwait {
            debugger!!.evaluateExpression("(1 + 2 * 3) :: Int", object : XDebuggerEvaluator.XEvaluationCallback {
                override fun evaluated(result: XValue) = withSignal { evalResult = result }
                override fun errorOccurred(errorMessage: String) = withSignal { evalResult = null }
            })
        }
        Assert.assertTrue(evalResult is HsDebugValue)
        with (evalResult as HsDebugValue) {
            Assert.assertEquals("Int", binding.typeName)
            Assert.assertEquals("_", binding.value)
        }
    }

    @Test fun forceTest() {
        var forceResult: LocalBinding? = null
        withAwait { debugger!!.setBreakpoint("Main", QSORT_LINE) }
        withAwait { debugger!!.trace(null) }
        withAwait {
            debugger!!.force("left", object : CommandCallback<LocalBinding?>() {
                override fun execAfterParsing(result: LocalBinding?) = withSignal { forceResult = result }
            })
        }
        Assert.assertEquals("[4,2,3]", forceResult?.value)
    }
}