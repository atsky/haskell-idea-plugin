package org.jetbrains.haskell.run.cmd;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.icons.HaskellIcons;

import javax.swing.*;

public final class CmdLineRunConfigurationType implements ConfigurationType {

    public static final CmdLineRunConfigurationType INSTANCE = new CmdLineRunConfigurationType();

    private final ConfigurationFactory myFactory;

    public CmdLineRunConfigurationType() {
        this.myFactory = new ConfigurationFactoryEx(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new CmdLineRunConfiguration(project, this);
            }
        };
    }

    public String getDisplayName() {
        return "Cmd line";
    }

    public String getConfigurationTypeDescription() {
        return "Cmd line application";
    }

    public Icon getIcon() {
        return HaskellIcons.APPLICATION;
    }

    @NotNull
    public String getId() {
        return "CmdLineRunConfiguration";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] {myFactory};
    }
}
