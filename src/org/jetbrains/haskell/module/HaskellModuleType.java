package org.jetbrains.haskell.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.haskell.fileType.HaskellFileType;

import javax.swing.*;

public final class HaskellModuleType extends ModuleType<HaskellModuleBuilder> {

    public static final HaskellModuleType INSTANCE = new HaskellModuleType();

    public HaskellModuleType() {
        super("HASKELL_MODULE");
    }

    public HaskellModuleBuilder createModuleBuilder() {
        return new HaskellModuleBuilder();
    }

    public String getName() {
        return "Haskell Module";
    }

    public String getDescription() {
        return "Haskell module";
    }

    public Icon getBigIcon() {
        return IconLoader.findIcon("/org/jetbrains/haskell/haskell.png");
    }

    public Icon getNodeIcon(boolean isOpened) {
        return HaskellFileType.INSTANCE.getIcon();
    }

    public static ModuleType<?> get(Module module) {
        return ModuleType.get(module);
    }
}
