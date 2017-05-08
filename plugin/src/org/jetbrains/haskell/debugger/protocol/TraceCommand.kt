package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo

/**
 * Created by vlad on 7/10/14.
 */

class TraceCommand(val function: String = "main", callback: CommandCallback<HsStackFrameInfo?>?, vararg val params: String)
: FlowCommand(callback) {

    override fun getText(): String {
        val builder = StringBuilder()
        builder.append(":trace ").append(function)
        for (p in params) {
            builder.append(' ').append(p)
        }
        builder.append('\n')
        return builder.toString()
    }

}