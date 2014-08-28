package org.jetbrains.haskell.debugger.breakpoints

/**
 * Created by vlad on 8/27/14.
 */

public data class HaskellLineBreakpointDescription(public val module: String,
                                              public val line: Int,
                                              public val condition: String?)