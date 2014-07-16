package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.commands.AbstractCommand

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
}