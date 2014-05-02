package org.jetbrains.haskell.sdk

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import org.jdom.Element
import org.jetbrains.annotations.Nullable
import org.jetbrains.haskell.icons.HaskellIcons
import org.jetbrains.haskell.util.ProcessRunner
import javax.swing.*
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.util.*

public class HaskellSdkType() : SdkType("GHC") {

    override fun suggestHomePath(): String? {
        val versionsRoot: File
        val versions: Array<String>
        val append: String?
        if (SystemInfo.isLinux) {
            versionsRoot = File("/usr/lib")
            if (!versionsRoot.isDirectory())
                return null
            versions = versionsRoot.list(object : FilenameFilter {

                override fun accept(dir: File, name: String): Boolean {
                    return name.toLowerCase().startsWith("ghc") && File(dir, name).isDirectory()
                }
            })!!
            append = null
        } else
            if (SystemInfo.isWindows) {
                var progFiles = System.getenv("ProgramFiles(x86)")
                if (progFiles == null) {
                    progFiles = System.getenv("ProgramFiles")
                }
                if (progFiles == null)
                    return null
                versionsRoot = File(progFiles, "Haskell Platform")
                if (!versionsRoot.isDirectory())
                    return progFiles
                versions = versionsRoot.list()!!
                append = null
            } else
                if (SystemInfo.isMac) {
                    versionsRoot = File("/Library/Frameworks/GHC.framework/Versions/")
                    if (!versionsRoot.isDirectory())
                        return null
                    versions = versionsRoot.list()!!
                    append = "usr"
                } else {
                    return null
                }
        val latestVersion = getLatestVersion(versions)
        if (latestVersion == null)
            return null
        val versionDir = File(versionsRoot, latestVersion)
        val homeDir: File
        if (append != null) {
            homeDir = File(versionDir, append)
        } else {
            homeDir = versionDir
        }
        return homeDir.getAbsolutePath()
    }

    override fun isValidSdkHome(path: String?): Boolean {
        return checkForGhc(File(path!!))
    }

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String?): String {
        val suggestedName: String
        if (currentSdkName != null && currentSdkName.length() > 0) {
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
        val versionString : String? = getGhcVersion(sdkHome!!)
        if (versionString != null && versionString.length() == 0) {
            return null
        }

        return versionString
    }

    override fun createAdditionalDataConfigurable(sdkModel: SdkModel?,
                                                  sdkModificator: SdkModificator?): AdditionalDataConfigurable? {
        return null; //HaskellSdkConfigurable()
    }


    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
    }


    override fun loadAdditionalData(additional: Element?): SdkAdditionalData? {
        return null
    }
    override fun getPresentableName(): String {
        return "GHC"
    }

    override fun getIcon(): Icon {
        return GHC_ICON
    }

    override fun getIconForAddAction(): Icon {
        return getIcon()
    }

    override fun setupSdkPaths(sdk: Sdk) {
    }

    override fun isRootTypeApplicable(`type`: OrderRootType?): Boolean {
        return false
    }


    class object {

        public val INSTANCE: HaskellSdkType = HaskellSdkType()
        private val GHC_ICON: Icon = HaskellIcons.HASKELL

        private fun getLatestVersion(names: Array<String>?): String? {
            if (names == null)
                return null
            val length = names.size
            if (length == 0)
                return null
            if (length == 1)
                return names[0]
            val ghcDirs = ArrayList<GHCDir>()
            for (name in names) {
                ghcDirs.add(GHCDir(name))
            }
            Collections.sort(ghcDirs, object : Comparator<GHCDir> {
                override fun compare(d1: GHCDir, d2: GHCDir): Int {
                    return d1.version.compareTo(d2.version)
                }
            })
            return ghcDirs.get(ghcDirs.size() - 1).name
        }

        public fun checkForGhc(path: File): Boolean {
            val bin = File(path, "bin")
            if (!bin.isDirectory())
                return false
            val children = bin.listFiles(object : FileFilter {
                override fun accept(f: File): Boolean {
                    if (f.isDirectory())
                        return false
                    return "ghc".equalsIgnoreCase(FileUtil.getNameWithoutExtension(f))
                }
            })
            return children != null && children.size >= 1
        }

        public fun getGhcVersion(homePath: String): String? {
            if (homePath == null || !File(homePath).isDirectory()) {
                return null
            }
            try {
                val cmd = Arrays.asList(homePath + File.separator + "bin" + File.separator + "ghc", "--numeric-version")
                return ProcessRunner(null).execute(cmd).trim()
            } catch (ex: Exception) {
                // ignore
            }

            return null
        }
    }
}
