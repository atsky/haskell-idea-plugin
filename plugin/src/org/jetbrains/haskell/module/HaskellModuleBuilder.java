package org.jetbrains.haskell.module;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.haskell.sdk.HaskellSdkType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class HaskellModuleBuilder extends ModuleBuilder {

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return StdModuleTypes.JAVA.modifySettingsStep(settingsStep, this);
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return getModuleType().createWizardSteps(wizardContext, this, modulesProvider);
    }

    @Override
    public HaskellModuleType getModuleType() {
        return HaskellModuleType.INSTANCE;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        if (myJdk != null) {
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }

        ContentEntry contentEntry = doAddContentEntry(rootModel);
        if (contentEntry != null) {
            String srcPath = getContentEntryPath() + File.separator + "src";
            new File(srcPath).mkdirs();
            final VirtualFile sourceRoot = LocalFileSystem.getInstance()
                    .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(srcPath));
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, false, "");
            }
            String name = getName();
            try {
                makeCabal(getContentEntryPath() + File.separator + name + ".cabal", name);
                makeMain(srcPath + File.separator + "Main.hs");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void makeCabal(String path, String name) throws IOException {
        final String text =
                    "name:              " + name + "\n" +
                    "version:           1.0\n" +
                    "Build-Type:        Simple\n" +
                    "cabal-version:     >= 1.2\n" +
                    "\n" +
                    "executable " + name + "\n" +
                    "  main-is:         Main.hs\n" +
                    "  hs-source-dirs:  src\n" +
                    "  build-depends:   base\n";
        FileWriter writer = new FileWriter(path);
        writer.write(text);
        writer.close();
    }

    public void makeMain(String path) throws IOException {
        final String text =
                "package Main\n" +
                "\n";

        FileWriter writer = new FileWriter(path);
        writer.write(text);
        writer.close();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof HaskellSdkType;
    }
}
