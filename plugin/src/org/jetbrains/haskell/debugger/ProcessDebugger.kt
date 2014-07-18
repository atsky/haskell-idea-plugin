package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HaskellStackFrameInfo

/**
 * Created by vlad on 7/11/14.
 */

public trait ProcessDebugger {

    public fun trace()

    public fun execute(command: AbstractCommand)

    public fun setBreakpoint(line: Int)

    public fun removeBreakpoint(breakpointNumber: Int)

    public fun close()

    public fun stepInto()

    public fun stepOver()

    public fun resume()

    public fun prepareGHCi()

    public fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?, topFrameInfo: HaskellStackFrameInfo)

    public fun requestVariables()
}