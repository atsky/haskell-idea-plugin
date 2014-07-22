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

/**
 * @author Habibullin Marat
 */

public class SequenceOfBacksCommand(val generalStackFrame: HsCommonStackFrame,
                                    val syncObject: Lock,
                                    val frameBindingsAreSet: Condition,
                                    val sequenceLength: Int,
                                    var currentStep: Int = sequenceLength - 1) : RealTimeCommand() {
    override fun getBytes(): ByteArray = ":back\n".toByteArray()
    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        if (currentStep != 0) {
            currentStep -= 1
            debugProcess.debugger.backsSequence(this)
        } else {
            val localBindings = Parser.tryParseLocalBindings(output)
            syncObject.lock()
            generalStackFrame.setBindings(localBindings)
            syncObject.unlock()
            debugProcess.debugger.forwardsSequence(SequenceOfForwardsCommand(frameBindingsAreSet, syncObject, sequenceLength))
        }
    }
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