package org.jetbrains.cabal.settings

import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings

public class CabalProjectSettings() : ExternalProjectSettings() {
//
//    private var myCabalHome: String? = null
//
//    public fun getCabalHome(): String? {
//        return myCabalHome
//    }
//
//    public fun setCabalHome(cabalHome: String?) {
//        myCabalHome = cabalHome
//    }

    override fun clone(): ExternalProjectSettings {
        val result = CabalProjectSettings()
        copyTo(result)
//        result.myCabalHome = myCabalHome
        return result
    }
}