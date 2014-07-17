package org.jetbrains.haskell.debugger

/**
 * Created by marat-x on 7/17/14.
 */

public class HaskellUtils {
    class object {
        fun zeroBasedToHaskellLineNumber(zeroBasedFileLineNumber: Int) = zeroBasedFileLineNumber + 1
        fun haskellLineNumberToZeroBased(haskellFileLineNumber: Int) = haskellFileLineNumber - 1
    }
}