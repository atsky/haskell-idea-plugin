package org.jetbrains.haskell.util

import java.io.File

public fun joinPath(first : String, vararg more : String) : String {
    var result = first
    for (str in more) {
        result += File.separator + str
    }
    return result
}