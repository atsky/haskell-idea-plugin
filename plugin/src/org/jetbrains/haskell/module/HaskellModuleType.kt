package org.jetbrains.haskell.module

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import org.jetbrains.haskell.fileType.HaskellFileType
import org.jetbrains.haskell.icons.HaskellIcons
import javax.swing.*
import com.intellij.ide.util.projectWizard.ModuleBuilder

public class HaskellModuleType() : ModuleType<HaskellModuleBuilder>("HASKELL_MODULE") {

    override fun createModuleBuilder(): HaskellModuleBuilder {
        return HaskellModuleBuilder()
    }

    override fun getName(): String {
        return "Haskell Module"
    }

    override fun getDescription(): String {
        return "Haskell Module"
    }

    override fun getBigIcon(): Icon {
        return HaskellIcons.DEFAULT
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return HaskellFileType.INSTANCE.getIcon()
    }

    companion object {
        public val INSTANCE: HaskellModuleType = HaskellModuleType()
    }
}
