package org.jetbrains.haskell.module;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.ModifiableRootModel;

public final class HaskellModuleBuilder extends JavaModuleBuilder {

    @Override
    public HaskellModuleType getModuleType() {
        return HaskellModuleType.INSTANCE;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        ProjectJdkTable table = ProjectJdkTable.getInstance();
        super.setupRootModel(rootModel);
    }
}
