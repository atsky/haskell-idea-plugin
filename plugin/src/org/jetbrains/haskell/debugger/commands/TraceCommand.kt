package org.jetbrains.haskell.debugger.commands

/**
 * Created by vlad on 7/10/14.
 */

public class TraceCommand(val function: String = "main", vararg val params: String) : AbstractCommand() {

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