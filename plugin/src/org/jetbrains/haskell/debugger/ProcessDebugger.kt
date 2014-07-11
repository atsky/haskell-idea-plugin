package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.commands.AbstractCommand

/**
 * Created by vlad on 7/11/14.
 */

public trait ProcessDebugger {

    public fun waitForConnect()

    public fun run()

    public fun addBreakPoint(file: String, line: String)

    public fun removeBreakPoint(file: String, line: String)

    public fun execute(command: AbstractCommand)
}