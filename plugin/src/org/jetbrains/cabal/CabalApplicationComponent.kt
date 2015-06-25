package org.jetbrains.cabal

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.profile.ui.ProfileUIFactory
import org.jetbrains.haskell.util.OSUtil
import org.jetbrains.haskell.util.joinPath
import java.io.File
import kotlin.platform.platformStatic

/**
 * @author Evgeny.Kurbatsky
 * @since 6/15/15.
 */

class CabalApplicationComponent() : ApplicationComponent {
    companion object {
        platformStatic
        public fun getInstance(): CabalApplicationComponent =
                ApplicationManager.getApplication().getComponent(javaClass<CabalApplicationComponent>())
    }

    var configuration: CabalConfing? = null


    fun getCabalConfiguration(): CabalConfing {
        if (configuration == null) {
            val f = File(joinPath(OSUtil.getCabalConfig()))
            if (f.exists()) {
                configuration = CabalConfing.read(f)
            }
        }
        return configuration!!
    }

    override fun disposeComponent() {
    }

    override fun initComponent() {
        getCabalConfiguration()
    }

    override fun getComponentName(): String = "CabalApplicationComponent"


}