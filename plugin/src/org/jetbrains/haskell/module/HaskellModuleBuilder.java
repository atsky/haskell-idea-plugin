package org.jetbrains.haskell.module;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.haskell.sdk.HaskellSdkType;

public final class HaskellModuleBuilder extends JavaModuleBuilder {

    @Override
    public HaskellModuleType getModuleType() {
        return HaskellModuleType.INSTANCE;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        super.setupRootModel(rootModel);
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof HaskellSdkType;
    }
}
