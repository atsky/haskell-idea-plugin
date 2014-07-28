package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 7/17/14.
 */

/**
 * Command like setting breakpoint
 */
public abstract class RealTimeCommand<R : ParseResult?>(callback: CommandCallback<R>?) : AbstractCommand<R>(callback)