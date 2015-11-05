package org.jetbrains.haskell.sdk

import com.intellij.openapi.projectRoots.SdkAdditionalData
import org.jdom.Element


class HaskellSdkAdditionalData(ghciPath: String?,
                               ghcpkgPath: String?,
                               cabalPath: String?) : SdkAdditionalData {

    companion object {
        private val GHCI_PATH = "ghci_path"
        private val GHC_PKG_PATH = "ghcpkg_path"
        private val CABAL_PATH = "cabal_path"

        public fun load(element: Element): HaskellSdkAdditionalData {
            val data = HaskellSdkAdditionalData(null, null, null)
            data.ghciPath = element.getAttributeValue(GHCI_PATH)
            data.ghcPkgPath = element.getAttributeValue(GHC_PKG_PATH)
            data.cabalPath = element.getAttributeValue(CABAL_PATH)

            return data
        }

    }

    var ghciPath: String? = ghciPath
    var ghcPkgPath: String? = ghcpkgPath
    var cabalPath: String? = cabalPath

    override fun clone() : Any {
        throw CloneNotSupportedException()
    }

    fun save(element: Element): Unit {
        if (ghciPath != null) {
            element.setAttribute(GHC_PKG_PATH, ghcPkgPath)
        }
        if (ghcPkgPath != null) {
            element.setAttribute(GHC_PKG_PATH, ghcPkgPath)
        }
        if (cabalPath != null) {
            element.setAttribute(CABAL_PATH, cabalPath!!)
        }

    }
}
