package org.jetbrains.cabal.export

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalModuleBuilder
import com.intellij.openapi.module.*
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import org.jetbrains.haskell.module.HaskellModuleType
import org.jetbrains.cabal.settings.CabalProjectSettings
import org.jetbrains.cabal.util.*

import javax.swing.*
import java.io.File
import java.io.IOException

class CabalModuleBuilder : AbstractExternalModuleBuilder<CabalProjectSettings>(SYSTEM_ID, CabalProjectSettings()) {

    @Throws(ConfigurationException::class)
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel?) {
    }

    override fun getModuleType(): ModuleType<out ModuleBuilder> {
        return HaskellModuleType.INSTANCE
    }
}
