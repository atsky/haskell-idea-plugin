package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.xdebugger.breakpoints.XLineBreakpoint

/**
 * Created by vlad on 8/27/14.
 */

public class HaskellLineBreakpointDescription(public val module: String,
                                              public val line: Int,
                                              public val condition: String?)