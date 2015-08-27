package org.jetbrains.haskell.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by atsky on 27/08/15.
 */
public class DefaultListModelWrapper {
    private ListModel myModel;

    public DefaultListModelWrapper(ListModel model) {
        this.myModel = model;
    }


    public List<?> getElements() {
        return Arrays.asList(((DefaultListModel) myModel).toArray());
    }

    public void removeAllElements() {
        ((DefaultListModel) myModel).removeAllElements();
    }

    public void addElement(@NotNull String packageName) {
        ((DefaultListModel) myModel).addElement(packageName);

    }
}
