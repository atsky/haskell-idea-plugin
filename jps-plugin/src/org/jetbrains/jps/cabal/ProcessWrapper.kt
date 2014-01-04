package org.jetbrains.jps.cabal

import java.io.File
import java.io.IOException
import java.util.Arrays

public open class ProcessWrapper(val workingDirectory: String?) {

    public open fun getProcess(vararg cmd: String): Process {
        val processBuilder = ProcessBuilder(cmd.toList())
        if (workingDirectory != null) {
            processBuilder.directory(File(workingDirectory))
        }

        processBuilder.redirectErrorStream(true)
        return processBuilder.start()
    }


}
