package org.jetbrains.haskell.sdk

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.SdkModel
import org.jdom.Element

private val GHC_OPTIONS = "GhcOptions"
private val CABAL_PATH = "CabalPath"

public class HaskellSdkAdditionalData(ghcOptions: String?, cabalPath: String?) : SdkAdditionalData, Cloneable {
    private var myGhcOptions: String? = ghcOptions
    private var myCabalPath: String? = cabalPath

    public fun checkValid(sdkModel: SdkModel?): Unit {

    }

    public override fun clone() : Any {
        return super<Cloneable>.clone()
    }
    public fun save(element: Element): Unit {
        if (myGhcOptions != null)
        {
            element.setAttribute(GHC_OPTIONS, myGhcOptions)
            element.setAttribute(CABAL_PATH, myCabalPath)
        }

    }
    public fun getGhcOptions(): String? {
        return (if (myGhcOptions == null)
            ""
        else
            myGhcOptions)
    }
    public fun setGhcOptions(ghcOptions: String?): Unit {
        this.myGhcOptions = ghcOptions
    }
    public fun getCabalPath(): String {
        return if (myCabalPath == null) "" else myCabalPath!!
    }
    public fun setCabalPath(cabalPath: String?): Unit {
        this.myCabalPath = cabalPath
    }


    class object {
        public fun init(): HaskellSdkAdditionalData {
            return HaskellSdkAdditionalData(null, null)
        }
        public fun load(element: Element): HaskellSdkAdditionalData {
            val data = HaskellSdkAdditionalData(null, null)
            data.myCabalPath = element.getAttributeValue(CABAL_PATH)
            data.myGhcOptions = element.getAttributeValue(GHC_OPTIONS)
            return data
        }

    }
}
