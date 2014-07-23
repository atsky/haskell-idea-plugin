package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/17/14.
 */

/**
 * Command like setting breakpoint
 */
public abstract class RealTimeCommand(callback: CommandCallback?) : AbstractCommand(callback)