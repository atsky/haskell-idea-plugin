package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import org.jetbrains.haskell.debugger.frames.HsCommonStackFrame
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo

/**
 * @author Habibullin Marat
 */

public class SequenceOfBacksCommand(val allHistFramesArray: ArrayList<HsCommonStackFrameInfo>,
                                    val syncObject: Lock,
                                    val frameBindingsAreSet: Condition,
                                    val sequenceLength: Int,
                                    var currentStep: Int = sequenceLength - 1) : RealTimeCommand() {
    override fun getBytes(): ByteArray = ":back\n".toByteArray()
    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        if (currentStep != 0) {
            fillFrameBindingsIfNeeded(output)
            currentStep -= 1
            debugProcess.debugger.backsSequence(this)
        } else {
            fillLastFrameBindings(output)
            debugProcess.debugger.forwardsSequence(SequenceOfForwardsCommand(frameBindingsAreSet, syncObject, sequenceLength))
        }
    }

    private fun fillFrameBindingsIfNeeded(output: Deque<String?>) {
        val index = currentFrameIndexInFramesArray()
        if (allHistFramesArray.get(index).bindings == null) {
            allHistFramesArray.get(index).bindings = Parser.tryParseLocalBindings(output)
        }
    }

    private fun fillLastFrameBindings(output: Deque<String?>) {
        syncObject.lock()
        val index = currentFrameIndexInFramesArray()
        allHistFramesArray.get(index).bindings = Parser.tryParseLocalBindings(output)
        syncObject.unlock()
    }

    private fun currentFrameIndexInFramesArray() = sequenceLength - currentStep - 1
}

public class SequenceOfForwardsCommand(val frameBindingsAreSet: Condition,
                                       val syncObject: Lock,
                                       val sequenceLength: Int,
                                       var currentStep: Int = 1) : RealTimeCommand() {
    override fun getBytes(): ByteArray = ":forward\n".toByteArray()

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        if(currentStep != sequenceLength) {
            currentStep += 1
            debugProcess.debugger.forwardsSequence(this)
        } else {
            syncObject.lock()
            try {
                frameBindingsAreSet.signal()
            } finally {
                syncObject.unlock()
            }
        }
    }
}