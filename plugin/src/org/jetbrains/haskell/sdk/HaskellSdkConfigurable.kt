package org.jetbrains.haskell.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.SdkModificator
import org.jetbrains.haskell.util.OSUtil
import java.io.File
import javax.swing.*

class HaskellSdkConfigurable : AdditionalDataConfigurable {
    private val form: HaskellSdkConfigurableForm = HaskellSdkConfigurableForm()

    private var mySdk: Sdk? = null

    override fun setSdk(sdk: Sdk?) {
        mySdk = sdk
    }

    override fun createComponent(): JComponent {
        return form.getContentPanel()
    }

    override fun isModified(): Boolean {
        return form.isModified
    }

    override fun apply() {
        val newData = HaskellSdkAdditionalData(
                form.getGhciPath(),
                form.getGhcpkgPath(),
                form.getCabalPath())

        val modificator = mySdk!!.sdkModificator
        modificator.sdkAdditionalData = newData
        ApplicationManager.getApplication()!!.runWriteAction(object : Runnable {
            override fun run() {
                modificator.commitChanges()
            }
        })
        form.isModified = false
    }

    override fun reset() {
        val sdk = mySdk!!
        val data = sdk.sdkAdditionalData

        if (data != null) {
            if (data !is HaskellSdkAdditionalData) {
                return
            }
            val ghcData: HaskellSdkAdditionalData = data
            val ghciPath = ghcData.ghciPath ?: ""
            val ghcPkgPath = ghcData.ghcPkgPath ?: ""
            val cabalPath = ghcData.cabalPath ?: ""

            form.init(ghciPath, ghcPkgPath, cabalPath)
        } else {
            val file = File(sdk.homePath)
            val version = extractVersion(file.name)
            val parent = file.parent
            form.init(
                    File(parent, OSUtil.getExe("ghci-" + version)).toString(),
                    File(parent, OSUtil.getExe("ghc-pkg-" + version)).toString(),
                    File(parent, OSUtil.getExe("cabal")).toString())
        }

        form.isModified = false
    }

    private fun extractVersion(name: String) : String {
        val trimmedName = OSUtil.removeExtension(name)

        if (trimmedName == "ghc") {
            return ""
        }
        if (trimmedName.startsWith("ghc-")) {
            return name.substring(4)
        }
        return ""
    }

    override fun disposeUIResources() {
    }

}
