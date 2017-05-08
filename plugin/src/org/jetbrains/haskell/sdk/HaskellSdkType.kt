package org.jetbrains.haskell.sdk

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jdom.Element
import org.jetbrains.annotations.Nullable
import org.jetbrains.haskell.icons.HaskellIcons
import org.jetbrains.haskell.util.ProcessRunner
import javax.swing.*
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.util.*
import org.jetbrains.haskell.util.GHCVersion
import org.jetbrains.haskell.util.GHCUtil
import org.jetbrains.haskell.sdk.HaskellSdkType.SDKInfo
import kotlin.text.Regex

class HaskellSdkType : SdkType("GHC") {

    companion object {
        private val WINDOWS_EXECUTABLE_SUFFIXES = arrayOf("cmd", "exe", "bat", "com")
        val INSTANCE: HaskellSdkType = HaskellSdkType()
        private val GHC_ICON: Icon = HaskellIcons.HASKELL

        private fun getLatestVersion(ghcPaths: List<File>): SDKInfo? {
            val length = ghcPaths.size
            if (length == 0)
                return null
            if (length == 1)
                return SDKInfo(ghcPaths[0])
            val ghcDirs = ArrayList<SDKInfo>()
            for (name in ghcPaths) {
                ghcDirs.add(SDKInfo(name))
            }
            Collections.sort(ghcDirs, object : Comparator<SDKInfo> {
                override fun compare(d1: SDKInfo, d2: SDKInfo): Int {
                    return d1.version.compareTo(d2.version)
                }
            })
            return ghcDirs.get(ghcDirs.size - 1)
        }

        fun checkForGhc(path: String): Boolean {
            val file = File(path)
            if (file.isDirectory) {
                val children = file.listFiles(object : FileFilter {
                    override fun accept(f: File): Boolean {
                        if (f.isDirectory)
                            return false
                        return f.name == "ghc"
                    }
                })
                return children.isNotEmpty()
            } else {
                return isGhc(file.name)
            }
        }

        fun isGhc(name : String) : Boolean =
                name == "ghc" || name.matches("ghc-[.0-9*]+".toRegex())


        fun getGhcVersion(ghcPath: File): String? {
            if (ghcPath.isDirectory) {
                return null
            }
            try {
                return ProcessRunner(null).executeOrFail(ghcPath.toString(), "--numeric-version").trim()
            } catch (ex: Exception) {
                // ignore
            }

            return null
        }
    }

    class SDKInfo(val ghcPath: File) {
        val version: GHCVersion = getVersion(ghcPath.name)

        companion object {
            fun getVersion(name: String?): GHCVersion {
                val versionStr : List<String> = if (name == null) {
                    listOf<String>()
                } else {
                    name.split("[^0-9]+".toRegex()).filter { !it.isEmpty() }
                }
                val parts = ArrayList<Int>()
                for (part in versionStr) {
                    if (part.isEmpty())
                        continue
                    try {
                        parts.add(part.toInt())
                    } catch (nfex: NumberFormatException) {
                        // ignore
                    }

                }
                return GHCVersion(parts)
            }
        }

    }

    override fun getHomeChooserDescriptor(): FileChooserDescriptor {
        val isWindows = SystemInfo.isWindows
        return object : FileChooserDescriptor(true, false, false, false, false, false) {
            @Throws(Exception::class)
            override fun validateSelectedFiles(files: Array<VirtualFile>?) {
                if (files!!.size != 0) {
                    if (!isValidSdkHome(files[0].path)) {
                        throw Exception("Not valid ghc " + files[0].name)
                    }
                }
            }

            override fun isFileVisible(file: VirtualFile, showHiddenFiles: Boolean): Boolean {
                if (!file.isDirectory) {
                    if (!file.name.toLowerCase().startsWith("ghc")) {
                        return false
                    }
                    if (isWindows) {
                        val path = file.path
                        var looksExecutable = false
                        for (ext in WINDOWS_EXECUTABLE_SUFFIXES) {
                            if (path.endsWith(ext)) {
                                looksExecutable = true
                                break
                            }
                        }
                        return looksExecutable && super.isFileVisible(file, showHiddenFiles)
                    }
                }
                return super.isFileVisible(file, showHiddenFiles)
            }
        }.withTitle("Select GHC executable").withShowHiddenFiles(SystemInfo.isUnix)
    }

    override fun suggestHomePath(): String? {
        val versions: List<File>
        if (SystemInfo.isLinux) {
            val versionsRoot = File("/usr/bin")
            if (!versionsRoot.isDirectory) {
                return null
            }
            versions = (versionsRoot.listFiles(object : FilenameFilter {
                override fun accept(dir: File, name: String): Boolean {
                    return !File(dir, name).isDirectory && isGhc(name.toLowerCase())
                }
            })?.toList() ?: listOf())
        } else if (SystemInfo.isWindows) {
            throw UnsupportedOperationException()
            /*
            var progFiles = System.getenv("ProgramFiles(x86)")
            if (progFiles == null) {
                progFiles = System.getenv("ProgramFiles")
            }
            if (progFiles == null)
                return null
            val versionsRoot = File(progFiles, "Haskell Platform")
            if (!versionsRoot.isDirectory)
                return progFiles
            versions = versionsRoot.listFiles()?.toList() ?: listOf()
            */
        } else if (SystemInfo.isMac) {
            throw UnsupportedOperationException()
            /*
            val macVersions = ArrayList<File>()
            val versionsRoot = File("/Library/Frameworks/GHC.framework/Versions/")
            if (versionsRoot.isDirectory()) {
                macVersions.addAll(versionsRoot.listFiles()?.toList() ?: listOf())
            }
            val brewVersionsRoot = File("/usr/local/Cellar/ghc")
            if (brewVersionsRoot.isDirectory()) {
                macVersions.addAll(brewVersionsRoot.listFiles()?.toList() ?: listOf())
            }
            versions = macVersions
            */
        } else {
            return null
        }
        val latestVersion = getLatestVersion(versions)
        
        return latestVersion?.ghcPath?.absolutePath
    }

    override fun isValidSdkHome(path: String?): Boolean {
        return checkForGhc(path!!)
    }

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String?): String {
        val suggestedName: String
        if (currentSdkName != null && currentSdkName.length > 0) {
            suggestedName = currentSdkName
        } else {
            val versionString = getVersionString(sdkHome)
            if (versionString != null) {
                suggestedName = "GHC " + versionString
            } else {
                suggestedName = "Unknown"
            }
        }
        return suggestedName
    }

    override fun getVersionString(sdkHome: String?): String? {
        if (sdkHome == null) {
            return null
        }
        val versionString: String? = getGhcVersion(File(sdkHome))
        if (versionString != null && versionString.length == 0) {
            return null
        }

        return versionString
    }

    override fun createAdditionalDataConfigurable(sdkModel: SdkModel,
                                                  sdkModificator: SdkModificator): AdditionalDataConfigurable {
        return HaskellSdkConfigurable()
    }


    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        if (additionalData is HaskellSdkAdditionalData) {
            additionalData.save(additional)
        }
    }


    override fun loadAdditionalData(additional: Element?): SdkAdditionalData? {
        return HaskellSdkAdditionalData.load(additional!!)
    }
    override fun getPresentableName(): String {
        return "GHC"
    }

    override fun getIcon(): Icon {
        return GHC_ICON
    }

    override fun getIconForAddAction(): Icon {
        return icon
    }

    override fun setupSdkPaths(sdk: Sdk) {
    }

    override fun isRootTypeApplicable(rootType: OrderRootType): Boolean {
        return false
    }

}
