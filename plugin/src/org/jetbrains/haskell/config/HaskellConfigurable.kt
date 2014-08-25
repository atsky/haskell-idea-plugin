package org.jetbrains.haskell.config

import com.intellij.openapi.options.Configurable
import org.jetbrains.haskell.icons.HaskellIcons
import javax.swing.*
import com.intellij.ui.DocumentAdapter
import javax.swing.event.DocumentEvent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import org.jetbrains.haskell.util.setConstraints
import java.awt.GridBagConstraints
import org.jetbrains.haskell.util.gridBagConstraints
import java.awt.Insets
import java.awt.GridBagLayout
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.ui.components.JBCheckBox
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent

public class HaskellConfigurable() : Configurable {
    private var isModified = false
    private val cabalPathField = TextFieldWithBrowseButton()
    private val cabalDataPathField = TextFieldWithBrowseButton()
    private val ghcMod = TextFieldWithBrowseButton()
    private val ghcModi = TextFieldWithBrowseButton()
    private val useGhcMod = JBCheckBox("Use ghc-mod automatic check (turn off if you have problems with ghc-mod)")
    private val usePty = JBCheckBox("Use pseudo terminal for project running")


    override fun getDisplayName(): String {
        return "Haskell"
    }

    override fun isModified(): Boolean = isModified

    override fun createComponent(): JComponent {

        cabalPathField.addBrowseFolderListener(
                "Select cabal execurtable",
                null,
                null,
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor())

        cabalDataPathField.addBrowseFolderListener(
                "Select data cabal directory",
                null,
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor())

        ghcMod.addBrowseFolderListener(
                "Select ghc-mod executable",
                null,
                null,
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor())

        val result = JPanel(GridBagLayout())

        val listener : DocumentAdapter = object : DocumentAdapter() {

            override fun textChanged(e: DocumentEvent?) {
                isModified = true;
            }

        };

        cabalPathField.getTextField()!!.getDocument()!!.addDocumentListener(listener)
        cabalDataPathField.getTextField()!!.getDocument()!!.addDocumentListener(listener)
        ghcMod.getTextField()!!.getDocument()!!.addDocumentListener(listener)

        val base = gridBagConstraints {
            insets = Insets(2, 0, 2, 3)
        }


        fun addLabeledControl(row : Int, label : String, component : JComponent) {
            result.add(JLabel(label), base.setConstraints {
                anchor = GridBagConstraints.LINE_START
                gridx = 0;
                gridy = row;
            })

            result.add(component, base.setConstraints {
                gridx = 1;
                gridy = row;
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            })

            result.add(Box.createHorizontalStrut(1), base.setConstraints {
                gridx = 2;
                gridy = row;
                weightx = 0.1
            })
        }

        addLabeledControl(0, "cabal executable", cabalPathField)
        addLabeledControl(1, "cabal data path", cabalDataPathField)
        addLabeledControl(2, "ghc-mod executable", ghcMod)
        addLabeledControl(3, "ghc-modi executable", ghcModi)

        val defaultChangeListener = object : ChangeListener {
            override fun stateChanged(p0: ChangeEvent) {
                isModified = true;
            }
        }
        useGhcMod.addChangeListener(defaultChangeListener)
        usePty.addChangeListener(defaultChangeListener)

        result.add(useGhcMod, base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridwidth = 2;
            gridy = 4;
        })
        result.add(usePty, base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridwidth = 2;
            gridy = 5;
        })


        result.add(Box.createVerticalStrut(1), base.setConstraints {
            gridy = 6;
            weighty = 2.0;
        })


        return result
    }

    public fun getIcon(): Icon {
        return HaskellIcons.HASKELL
    }

    override fun apply() {
        val state = HaskellSettings.getInstance().getState()
        state.cabalPath = cabalPathField.getTextField()!!.getText()
        state.cabalDataPath = cabalDataPathField.getTextField()!!.getText()
        state.ghcModPath = ghcMod.getTextField()!!.getText()
        state.ghcModiPath = ghcModi.getTextField()!!.getText()
        state.useGhcMod = useGhcMod.isSelected()
        state.usePty = usePty.isSelected()

        isModified = false
    }

    override fun disposeUIResources() {
    }

    override fun getHelpTopic(): String? = null

    override fun reset() {
        val state = HaskellSettings.getInstance().getState()
        cabalPathField.getTextField()!!.setText(state.cabalPath ?: "")
        cabalDataPathField.getTextField()!!.setText(state.cabalDataPath ?: "")
        ghcMod.getTextField()!!.setText(state.ghcModPath ?: "")
        ghcModi.getTextField()!!.setText(state.ghcModiPath ?: "")
        useGhcMod.setSelected(state.useGhcMod!!)
        usePty.setSelected(state.usePty!!)

        isModified = false
    }


}
