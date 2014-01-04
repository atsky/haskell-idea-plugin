package org.jetbrains.jps.cabal

import org.jetbrains.jps.ModuleChunk
import org.jetbrains.jps.builders.DirtyFilesHolder
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor
import org.jetbrains.jps.incremental.*
import org.jetbrains.jps.incremental.messages.BuildMessage
import org.jetbrains.jps.incremental.messages.CompilerMessage
import org.jetbrains.jps.incremental.messages.ProgressMessage
import org.jetbrains.jps.model.module.JpsModule
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import org.jetbrains.jps.incremental.ModuleLevelBuilder.ExitCode
import org.jetbrains.jps.incremental.ModuleLevelBuilder.OutputConsumer


public class CabalBuilder() : ModuleLevelBuilder(BuilderCategory.TRANSLATOR) {
    public override fun build(context: CompileContext?,
                              chunk: ModuleChunk?,
                              dirtyFilesHolder: DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget>?,
                              outputConsumer: OutputConsumer?): ExitCode {
        try {
            for (module in chunk!!.getModules()!!) {
                val cabalFile = getCabalFile(module)
                val cabal = CabalJspInterface(cabalFile!!)
                if (runConfinguration(context!!, cabal))
                    return ModuleLevelBuilder.ExitCode.ABORT

                if (runBuild(context, cabal))
                    return ModuleLevelBuilder.ExitCode.ABORT

            }
            return ModuleLevelBuilder.ExitCode.OK
        }
        catch (e: InterruptedException) {
            e.printStackTrace()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        return ModuleLevelBuilder.ExitCode.ABORT
    }

    private fun runBuild(context: CompileContext, cabal: CabalJspInterface): Boolean {
        context.processMessage(ProgressMessage("Cabal build"))
        val buildProcess = cabal.build()!!
        processCabalOutput(context, collectOutput(buildProcess))
        if (buildProcess.waitFor() != 0) {
            context.processMessage(CompilerMessage("cabal", BuildMessage.Kind.ERROR, "build errors."))
            return true
        }

        return false
    }

    private fun runConfinguration(context: CompileContext, cabal: CabalJspInterface): Boolean {
        context.processMessage(CompilerMessage("cabal", BuildMessage.Kind.INFO, "Start configure"))
        val configureProcess = cabal.configure()!!
        processCabalOutput(context, collectOutput(configureProcess))
        if (configureProcess.waitFor() != 0) {
            context.processMessage(CompilerMessage("cabal", BuildMessage.Kind.ERROR, "configure failed."))
            return true
        }

        context.processMessage(CompilerMessage("cabal", BuildMessage.Kind.INFO, "Start build"))
        return false
    }
    private fun processCabalOutput(context: CompileContext, processOut: Iterator<String>): Unit {
        while (processOut.hasNext()!!)
        {
            val line = processOut.next()
            val warningPrefix = "Warning: "
            if (line.startsWith(warningPrefix)!!)
            {
                val text = line.substring(warningPrefix.length()!!) + "\n" + processOut.next()
                context.processMessage(CompilerMessage("cabal", BuildMessage.Kind.WARNING, text))
            }
            else
            {
                context.processMessage(CompilerMessage("cabal", BuildMessage.Kind.INFO, line))
            }
        }
    }
    private fun collectOutput(process: Process): Iterator<String> {
        val reader = BufferedReader(InputStreamReader(process.getInputStream()!!))
        return object : Iterator<String> {
            var line: String? = null

            public override fun hasNext(): Boolean {
                return fetch() != null
            }

            private fun fetch(): String? {
                if (line == null)
                {
                    try
                    {
                        line = reader.readLine()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                return line
            }

            public override fun next(): String {
                val result = fetch()
                line = null
                return result!!
            }

        }
    }
    private fun getCabalFile(module: JpsModule): File? {
        val url = module.getContentRootsList().getUrls().get(0)
        try {
            for (file in File(URL(url).getFile()!!).listFiles()!!) {
                if (file.getName().endsWith(".cabal")) {
                    return file
                }

            }
        }
        catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return null
    }
    public override fun getCompilableFileExtensions(): MutableList<String> {
        return ArrayList(Arrays.asList("hs"))
    }
    public override fun toString(): String {
        return getPresentableName()
    }
    public override fun getPresentableName(): String {
        return "Cabal builder"
    }

}
