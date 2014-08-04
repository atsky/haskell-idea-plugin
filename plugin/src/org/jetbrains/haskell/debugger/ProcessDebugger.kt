package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.protocol.SequenceOfBacksCommand
import org.jetbrains.haskell.debugger.protocol.SequenceOfForwardsCommand
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.protocol.ForceCommand

/**
 * Created by vlad on 7/11/14.
 */

public trait ProcessDebugger {

    public fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback)

    public fun trace()

    public fun setBreakpoint(module: String, line: Int)

    public fun removeBreakpoint(module: String, breakpointNumber: Int)

    public fun close()

    public fun stepInto()

    public fun stepOver()

    public fun runToPosition(module: String, line: Int)

    public fun resume()

    public fun prepareDebugger()

    public fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?, topFrameInfo: HsTopStackFrameInfo)

    public fun back()

    public fun forward()

    public fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand)

    public fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand)

    public fun force(forceCommand: ForceCommand)

    public fun onTextAvailable(text: String, outputType: Key<out Any?>?)
}