package org.jetbrains.haskell.util;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.run.ModuleComboBoxRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by atsky on 27/08/15.
 */
public class JComboBoxWrapper {
    private JComboBox myComboBox;

    public JComboBoxWrapper(@NotNull JComboBox comboBox) {
        myComboBox = comboBox;
    }

    @NotNull
    public Object getSelectedItem() {
        return myComboBox.getSelectedItem();
    }

    public void setSelectedItem(@NotNull Object item) {
        myComboBox.setSelectedItem(item);
    }

    public void setRenderer(@NotNull ModuleComboBoxRenderer moduleComboBoxRenderer) {
        myComboBox.setRenderer(moduleComboBoxRenderer);
    }

    @NotNull
    public Component get() {
        return myComboBox;
    }
}
