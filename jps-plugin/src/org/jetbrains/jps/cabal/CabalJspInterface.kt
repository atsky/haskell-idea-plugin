package org.jetbrains.jps.cabal

import java.io.File
import java.io.IOException

public class CabalJspInterface(val cabalFile: File) {

    private fun runCommand(command: String): Process {
        return ProcessWrapper(cabalFile.getParentFile()!!.getCanonicalPath()).getProcess("cabal", command)
    }

    public fun configure() : Process = runCommand("configure")

    public fun build(): Process = runCommand("build")

    public fun clean(): Process = runCommand("clean")

}
