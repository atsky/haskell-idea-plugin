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
import org.jetbrains.haskell.util.GHCVersion
import org.jetbrains.haskell.util.GHCUtil
import org.jetbrains.haskell.sdk.HaskellSdkType.SDKInfo

public class HaskellSdkType() : SdkType("GHC") {

    class SDKInfo(val sdkPath : File) {
        val ghcHome: File
        val version: GHCVersion = GHCUtil.getVersion(sdkPath.getName());

        {
            ghcHome = if (SystemInfo.isMac && sdkPath.getAbsolutePath().contains("GHC.framework")) {
                File(sdkPath, "usr")
            } else {
                sdkPath
            }
        }
    }

    override fun suggestHomePath(): String? {
        val versions: List<File>
        if (SystemInfo.isLinux) {
            val versionsRoot = File("/usr/lib")
            if (!versionsRoot.isDirectory()) {
                return null
            }
            versions = (versionsRoot.listFiles(object : FilenameFilter {
                override fun accept(dir: File, name: String): Boolean {
                    return name.toLowerCase().startsWith("ghc") && File(dir, name).isDirectory()
                }
            })?.toList() ?: listOf())
        } else if (SystemInfo.isWindows) {
            var progFiles = System.getenv("ProgramFiles(x86)")
            if (progFiles == null) {
                progFiles = System.getenv("ProgramFiles")
            }
            if (progFiles == null)
                return null
            val versionsRoot = File(progFiles, "Haskell Platform")
            if (!versionsRoot.isDirectory())
                return progFiles
            versions = versionsRoot.listFiles()?.toList() ?: listOf()
        } else if (SystemInfo.isMac) {
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
        } else {
            return null
        }
        val latestVersion = getLatestVersion(versions)
        if (latestVersion == null)
            return null

        return latestVersion.ghcHome.getAbsolutePath()
    }

    override fun isValidSdkHome(path: String?): Boolean {
        return checkForGhc(path!!)
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
        val versionString: String? = getGhcVersion(sdkHome)
        if (versionString != null && versionString.length() == 0) {
            return null
        }

        return versionString
    }

    override fun createAdditionalDataConfigurable(sdkModel: SdkModel?,
                                                  sdkModificator: SdkModificator?): AdditionalDataConfigurable? {
        return HaskellSdkConfigurable();
    }


    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        if (additionalData is HaskellSdkAdditionalData) {
            additionalData.save(additional)
        }
    }


    override fun loadAdditionalData(additional: Element?): SdkAdditionalData? {
        return null;//HaskellSdkAdditionalData.load(additional!!);
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

        fun getBinDirectory(path: String) :  File {
            return File(path, "bin")

        }

        private fun getLatestVersion(sdkPaths: List<File>): SDKInfo? {
            val length = sdkPaths.size()
            if (length == 0)
                return null
            if (length == 1)
                return SDKInfo(sdkPaths[0])
            val ghcDirs = ArrayList<SDKInfo>()
            for (name in sdkPaths) {
                ghcDirs.add(SDKInfo(name))
            }
            Collections.sort(ghcDirs, object : Comparator<SDKInfo> {
                override fun compare(d1: SDKInfo, d2: SDKInfo): Int {
                    return d1.version.compareTo(d2.version)
                }
            })
            return ghcDirs.get(ghcDirs.size() - 1)
        }

        public fun checkForGhc(path: String): Boolean {
            val bin = getBinDirectory(path)
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

        public fun getGhcVersion(homePath: String?): String? {
            if (homePath == null || !File(homePath).isDirectory()) {
                return null
            }
            try {
                val cmd = getBinDirectory(homePath).getAbsolutePath() + File.separator + "ghc"
                return ProcessRunner(null).executeOrFail(cmd, "--numeric-version").trim()
            } catch (ex: Exception) {
                // ignore
            }

            return null
        }
    }
}
