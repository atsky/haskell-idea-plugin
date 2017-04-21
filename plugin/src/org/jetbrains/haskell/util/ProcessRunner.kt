package org.jetbrains.haskell.util

import java.io.*
import java.util.ArrayList

class ProcessRunner(workingDirectory: String? = null) {
    private val myWorkingDirectory: String? = workingDirectory

    fun executeNoFail(vararg cmd: String): String {
        return executeNoFail(cmd.toList(), null)
    }

    fun executeNoFail(cmd: List<String>, input: String?): String {
        try {
            return executeOrFail(cmd, input)
        } catch (e: IOException) {
            return ""
        }
    }

    fun executeOrFail(vararg cmd: String): String {
        return executeOrFail(cmd.toList(), null)
    }


    fun executeOrFail(cmd: List<String>, input: String?): String {
        val process = getProcess(cmd.toList())
        if (input != null) {
            val streamWriter = OutputStreamWriter(process.outputStream!!)
            streamWriter.write(input)
            streamWriter.close()
        }

        var myInput: InputStream = process.inputStream!!
        val data = readData(myInput)

        process.waitFor()

        return data
    }

    fun getProcess(cmd: List<String>, path: String? = null): Process {
        val processBuilder: ProcessBuilder = ProcessBuilder(cmd)

        if (path != null) {
            val environment = processBuilder.environment()!!
            environment.put("PATH", environment.get("PATH") + ":" + path)
        }

        if (OSUtil.isMac) {
            // It's hack to make homebrew based HaskellPlatform work
            val environment = processBuilder.environment()!!
            environment.put("PATH", environment.get("PATH") + ":/usr/local/bin")
        }

        if (myWorkingDirectory != null) {
            processBuilder.directory(File(myWorkingDirectory))
        }

        processBuilder.redirectErrorStream(true)
        return processBuilder.start()
    }


    fun readData(input: InputStream, callback: Callback): Unit {
        val reader = BufferedReader(InputStreamReader(input))
        while (true) {
            var line = reader.readLine()
            if (line == null) {
                return
            }

            callback.call(line)
        }
    }

    private fun readData(input: InputStream): String {
        val builder = StringBuilder()
        readData(input, object : Callback {
            override fun call(command: String?): Boolean {
                builder.append(command).append("\n")
                return true
            }
        })
        return builder.toString()
    }

    interface Callback {
        fun call(command: String?): Boolean
    }

}
