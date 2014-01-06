package org.jetbrains.haskell.util

import java.io.*
import java.util.ArrayList

public class ProcessRunner(workingDirectory: String?) {
    private val myWorkingDirectory: String? = workingDirectory


    public fun execute(cmd: Array<String>): String {
        return execute(cmd, null)
    }


    public fun execute(cmd: Array<String>, input: String?): String {
        try {
            val process = getProcess(cmd.toList())
            if (input != null) {
                val streamWriter = OutputStreamWriter(process.getOutputStream()!!)
                streamWriter.write(input)
                streamWriter.close()
            }

            var myInput: InputStream = process.getInputStream()!!
            process.waitFor()
            return readData(myInput)
        }
        catch (e: InterruptedException) {
            e.printStackTrace()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }

    public fun getProcess(cmd: List<String>): Process {
        val processBuilder: ProcessBuilder = ProcessBuilder(cmd)
        if (myWorkingDirectory != null)
        {
            processBuilder.directory(File(myWorkingDirectory))
        }

        processBuilder.redirectErrorStream(true)
        return processBuilder.start()
    }

    class object {

        public open fun readData(input: InputStream, callback: Callback): Unit {
            val reader = BufferedReader(InputStreamReader(input))
            while (true) {
                var line = reader.readLine()
                if (line == null) {
                    return
                }

                callback.call(line)
            }
        }
        private open fun readData(input: InputStream): String {
            val builder = StringBuilder()
            readData(input, object : Callback {
                public override fun call(command: String?): Boolean {
                    builder.append(command).append("\n")
                    return true
                }


            })
            return builder.toString()
        }
        public trait Callback {
            public open fun call(command: String?): Boolean


        }
    }
}
