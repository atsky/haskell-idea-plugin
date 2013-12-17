package org.jetbrains.haskell.run;

import com.intellij.openapi.module.Module;
import com.intellij.ui.ListCellRendererWrapper;

import javax.swing.*;

public final class ModuleComboBoxRenderer extends ListCellRendererWrapper<Module> {

    @Override
    public void customize(JList list, Module value, int index, boolean selected, boolean hasFocus) {
        if (value == null) {
            setText("null");
        } else {
            setText(value.getName());
        }
    }
}
