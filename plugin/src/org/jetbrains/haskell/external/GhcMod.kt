package org.jetbrains.haskell.external

import org.jetbrains.haskell.util.ProcessRunner

/**
 * Created by atsky on 3/29/14.
 */

class GhcMod {
    val PROGRAM = System.getProperty("user.home") + "/.cabal/bin/ghc-mod"

    fun getModuleContent(module : String) : List<String> {
        val text = ProcessRunner(null).execute(listOf(PROGRAM, "browse", module))
        if (text.contains(":Error:")) {
            return text.split('\n').toList()
        } else {
            return listOf()
        }
    }

}