package org.jetbrains.haskell.debugger

/**
 * @author Habibullin Marat
 */
public class HaskellStackFrameInfo(public val threadId: String,
                                   public val id: String,
                                   public val name: String,
                                   public val position: Int) { }