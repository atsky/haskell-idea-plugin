package org.jetbrains.haskell.debugger.breakpoints

/**
 * Created by vlad on 8/27/14.
 */

data class HaskellLineBreakpointDescription(val module: String,
                                                   val line: Int,
                                                   val condition: String?)