package org.jetbrains.haskell.run.haskell

import com.intellij.execution.configuration.ConfigurationFactoryEx
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.jetbrains.haskell.icons.HaskellIcons
import javax.swing.*

public class HaskellRunConfigurationType() : ConfigurationType {

    private val myFactory: ConfigurationFactory

    override fun getDisplayName(): String {
        return "Haskell"
    }

    override fun getConfigurationTypeDescription(): String {
        return "Haskell application"
    }

    override fun getIcon(): Icon {
        return HaskellIcons.APPLICATION
    }

    override fun getId(): String {
        return "CabalRunConfiguration"
    }

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return array<ConfigurationFactory>(myFactory)
    }

    init {
        this.myFactory = object : ConfigurationFactoryEx(this) {
            override fun createTemplateConfiguration(project: Project?): RunConfiguration {
                return CabalRunConfiguration(project, this)
            }
        }
    }

    companion object {

        public val INSTANCE: HaskellRunConfigurationType = HaskellRunConfigurationType()
    }
}
