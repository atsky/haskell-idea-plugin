package org.jetbrains.haskell.run;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;

/**
 * @author Evgeny.Kurbatsky
 */
public class ProgramParamsPanel extends JPanel {
    private LabeledComponent<TextFieldWithBrowseButton> mainFileComponent;
    private JComboBox<Module> moduleComboBox;

    public ProgramParamsPanel(Module[] modules) {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        final TextFieldWithBrowseButton component = new TextFieldWithBrowseButton();
        mainFileComponent = LabeledComponent.create(component, "Main module");
        mainFileComponent.getComponent().addBrowseFolderListener("Main file", "Main File", null,
                new FileChooserDescriptor(true, false, false, false, true, false));

        moduleComboBox = new JComboBox<Module>(new DefaultComboBoxModel<Module>(modules));
        moduleComboBox.setRenderer(new ModuleComboBoxRenderer());
        this.add(mainFileComponent);
        this.add(moduleComboBox);
    }

    public void applyTo(HaskellRunConfiguration s) {
        s.setMainFile((Module)moduleComboBox.getSelectedItem(), mainFileComponent.getComponent().getText());
    }

    public void reset(HaskellRunConfiguration s) {
        mainFileComponent.getComponent().setText(s.getMainFile());
        moduleComboBox.setSelectedItem(s.getModule());
    }


}
