package org.jetbrains.haskell.sdk

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.SdkModel
import org.jdom.Element
import org.jetbrains.haskell.util.OS


public class HaskellSdkAdditionalData(cabalPath: String?,
                                      cabalLibPath: String?) : SdkAdditionalData, Cloneable {


    private var myCabalPath: String? = cabalPath
    private var myCabalDataPath: String? = cabalLibPath


    public override fun clone() : Any {
        return super<Cloneable>.clone()
    }

    public fun save(element: Element): Unit {
        if (myCabalPath != null) {
            element.setAttribute(CABAL_PATH, myCabalPath!!)
        }
        if (myCabalDataPath != null) {
            element.setAttribute(CABAL_DATA_PATH, myCabalDataPath!!)
        }
    }

    public fun getCabalPath(): String {
        return if (myCabalPath == null) "" else myCabalPath!!
    }

    public fun getCabalDataPath(): String {
        return if (myCabalDataPath == null) "" else myCabalDataPath!!
    }

    public fun setCabalPath(cabalPath: String?): Unit {
        this.myCabalPath = cabalPath
    }


    class object {
        private val CABAL_PATH = "cabal_path"
        private val CABAL_DATA_PATH = "cabal_data_path"

        fun getDefaultCabalPath() : String {
            return "/usr/bin/cabal"
        }

        fun getDefaultCabalDataPath() : String {
            return OS.getCabalData()
        }

        public fun load(element: Element): HaskellSdkAdditionalData {
            val data = HaskellSdkAdditionalData(null, null)
            data.myCabalPath = element.getAttributeValue(CABAL_PATH) ?: getDefaultCabalPath()
            data.myCabalDataPath = element.getAttributeValue(CABAL_DATA_PATH) ?: getDefaultCabalDataPath()
            return data
        }

    }

}
