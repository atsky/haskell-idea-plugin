package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.frames.HsCommonStackFrame
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.protocol.SequenceOfBacksCommand
import org.jetbrains.haskell.debugger.protocol.SequenceOfForwardsCommand

/**
 * Created by vlad on 7/11/14.
 */

public trait ProcessDebugger {

    public fun trace()

    public fun setBreakpoint(line: Int)

    public fun removeBreakpoint(breakpointNumber: Int)

    public fun close()

    public fun stepInto()

    public fun stepOver()

    public fun resume()

    public fun prepareGHCi()

    public fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?, topFrameInfo: HsTopStackFrameInfo)

    public fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand)
    public fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand)

    public fun requestVariables()

    public fun onTextAvailable(text: String, outputType: Key<out Any?>?)
}