package org.jetbrains.haskell.compiler

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.haskell.sdk.HaskellSdkAdditionalData
import org.jetbrains.haskell.sdk.HaskellSdkType
import org.jetbrains.haskell.util.ProcessRunner
import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.Iterator
import java.util.regex.Matcher
import java.util.regex.Pattern

public open class GHCInterface() {
    public open fun runGHC(module: Module, file: VirtualFile, outputDir: VirtualFile): List<GHCMessage> {
        val command: Array<String> = array<String>(getGHC(module), "-c", "-outputdir", outputDir?.getPath(), file?.getPath())
        val result: String = ProcessRunner().execute(command)!!
        System.out.println(result)
        val lines: List<String> = result.split("\n").toList()
        val iterator: java.util.Iterator<String> = lines.iterator()
        var messages: List<GHCMessage?>? = ArrayList<GHCMessage?>()
        while (iterator?.hasNext()!!)
        {
            var line: String? = iterator?.next()
            if (isError(line))
            {
                var matcher: Matcher? = Pattern.compile("(.*):(\\d+):(\\d+):")?.matcher(line)
                matcher?.find()
                var message: GHCMessage? = GHCMessage(matcher?.group(1), matcher?.group(2), matcher?.group(3))
                var msg: String? = ""
                while (iterator?.hasNext()!!)
                {
                    var msgLine: String? = iterator?.next()
                    msg += msgLine + "\n"
                    if ((msgLine?.trim()?.length())!! == 0)
                    {
                        break
                    }

                }
                message?.setText(msg)
                messages?.add(message)
            }

        }
        return messages
    }
    private open fun isError(line: String?): Boolean {
        return line?.matches(".*:.*:.*:")!!
    }

    class object {
        public open fun getGHC(module: Module): String {
            var sdk = ModuleRootManager.getInstance(module)!!.getSdk()!!
            if ((sdk.getSdkType() is HaskellSdkType)) {
                val homeDirectory : VirtualFile = sdk.getHomeDirectory()!!
                val ghc: String =
                if (System.getProperty("os.name")?.toLowerCase()?.contains("win")!!)
                    "ghc.exe"
                else
                    "ghc"

                return homeDirectory.toString() + File.separator + "bin" + File.separator + ghc
            }

            throw RuntimeException("Ghc not found!!!")
        }
        public open fun getRunGHC(): String {
            if (System.getProperty("os.name")?.toLowerCase()?.contains("win")!!) {
                return "C:\\Program Files (x86)\\Haskell Platform\\2013.2.0.0\\bin\\runghc.exe"
            } else {
                return "/usr/bin/runghc"
            }
        }
    }
}
