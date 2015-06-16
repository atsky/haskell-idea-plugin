package org.jetbrains.cabal

import java.io.File

/**
 * @author Evgeny.Kurbatsky
 * @since 6/15/15.
 */

class CabalConfing() {
    var remoteRepoCache : String? = null
        private set

    companion object {
        fun read(file : File) : CabalConfing {
            val config = CabalConfing()

            for (line in file.readLines()) {
                if (line.startsWith("--")) {
                    continue
                }
                val chunks = line.splitBy(": ")
                if (chunks.size() > 0) {
                    if (chunks[0] == "remote-repo-cache") {
                        config.remoteRepoCache = chunks[1]
                    }
                }
            }

            return config;
        }
    }
}