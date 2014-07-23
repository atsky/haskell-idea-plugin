package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/10/14.
 */

public class TraceCommand(val function: String = "main", callback: CommandCallback?,
                          vararg val params: String) : FlowCommand(callback) {

    override fun getBytes(): ByteArray {
        val builder = StringBuilder()
        builder.append(":trace ").append(function)
        for (p in params) {
            builder.append(' ').append(p)
        }
        builder.append('\n')
        return builder.toString().toByteArray()
    }

}