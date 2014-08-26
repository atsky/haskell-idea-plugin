package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import org.jetbrains.haskell.debugger.history.HistoryManager
import org.junit.Test
import org.junit.Before
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import java.util.Properties
import java.io.FileInputStream

public abstract class DebuggerTest<T : ProcessDebugger> {

    class object {

        public var properties: Properties? = null;

        {
            properties = Properties()
            val filePath = javaClass.getResource("/unittest.properties")?.getFile()
            assertNotNull(filePath)
            properties!!.load(FileInputStream(filePath!!))
        }

        public enum class Result {
            TRACE_FINISHED
            POSITION_REACHED
            BREAKPOINT_REACHED
            EXCEPTION_REACHED
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

        public class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>)


        public class TestRespondent(val syncObject: SyncObject) : DebugRespondent {
            public val moduleName: String = "Main"
            public var result: Result? = null
            public var context: HsSuspendContext? = null
            public var breakpoint: XBreakpoint<out XBreakpointProperties<*>?>? = null
            public val breakpoints: MutableMap<BreakpointPosition, BreakpointEntry> = hashMapOf()

            override fun traceFinished() = withSignal(syncObject) {
                result = Result.TRACE_FINISHED
            }

            override fun positionReached(context: HsSuspendContext) = withSignal(syncObject) {
                result = Result.POSITION_REACHED
                this.context = context
            }

            override fun breakpointReached(breakpoint: XBreakpoint<out XBreakpointProperties<*>?>,
                                           evaluatedLogExpression: String?, context: HsSuspendContext) = withSignal(syncObject) {
                result = Result.BREAKPOINT_REACHED
                this.breakpoint = breakpoint
                this.context = context
            }

            override fun exceptionReached(context: HsSuspendContext) = withSignal(syncObject) {
                result = Result.EXCEPTION_REACHED
                this.context = context
            }

            override fun getBreakpointAt(module: String, line: Int): XLineBreakpoint<XBreakpointProperties<*>>? {
                assert(module.equals(moduleName))
                return breakpoints.get(BreakpointPosition(module, line))?.breakpoint
            }

            override fun setBreakpointNumberAt(breakpointNumber: Int, module: String, line: Int) = withSignal(syncObject) {
                assert(module.equals(moduleName))
                val entry = breakpoints.get(BreakpointPosition(module, line))
                if (entry != null) {
                    entry.breakpointNumber = breakpointNumber
                }
            }

            override fun getHistoryManager(): HistoryManager? = null

            override fun getModuleByFile(filename: String): String = moduleName
        }

        public fun withSignal(syncObject: SyncObject, method: () -> Unit) {
            syncObject.lock()
            try {
                method()
            } finally {
                syncObject.signal()
                syncObject.unlock()
            }
        }

        public fun withAwait(syncObject: SyncObject, method: () -> Unit) {
            syncObject.lock()
            try {
                method()
            } finally {
                syncObject.await()
                syncObject.unlock()
            }
        }
    }

    public abstract fun createDebugger(file: File, respondent: DebugRespondent): T

    private var syncObject: SyncObject? = null
    private var debugger: T? = null
    private var respondent: TestRespondent? = null

    Before public fun setupDebugger() {
        val url = this.javaClass.getResource("/TestMain.hs")
        val file = url?.getFile()
        assertNotNull(file)
        val testFile = File(file!!)
        assertTrue(testFile.exists())

        syncObject = SyncObject()
        respondent = TestRespondent(syncObject!!)

        debugger = createDebugger(testFile, respondent!!)

        debugger?.prepareDebugger()
    }

    Test public fun traceTest() {
        withAwait(syncObject!!) {
            debugger!!.trace(null)
        }
        assertEquals(Result.TRACE_FINISHED, respondent!!.result)
    }


}