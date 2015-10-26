package org.jetbrains.cabal

import org.jetbrains.haskell.util.OSUtil
import org.jetbrains.haskell.util.joinPath
import java.io.File

/**
 * @author Evgeny.Kurbatsky
 * @since 6/15/15.
 */

class CabalConfing() {
    var inizialized = false
    var remoteRepoCache: String = joinPath(OSUtil.getCabalData(), "packages")

    fun read(file: File) {
        for (line in file.readLines()) {
            if (line.startsWith("--")) {
                continue
            }
            val chunks = line.split(": ")
            if (chunks.size > 0) {
                if (chunks[0] == "remote-repo-cache") {
                    remoteRepoCache = chunks[1]
                }
            }
        }
    }

}