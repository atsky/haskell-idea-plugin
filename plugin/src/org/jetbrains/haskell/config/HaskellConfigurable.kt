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

public class HaskellConfigurable() : Configurable {
    private var isModified = false
    private val ghcMod: TextFieldWithBrowseButton = TextFieldWithBrowseButton()


    override fun getDisplayName(): String {
        return "Haskell"
    }

    override fun isModified(): Boolean = isModified

    override fun createComponent(): JComponent {
        val result = JPanel(GridBagLayout())

        val listener : DocumentAdapter = object : DocumentAdapter() {

            override fun textChanged(e: DocumentEvent?) {
                isModified = true;
            }

        };

        ghcMod.getTextField()!!.getDocument()!!.addDocumentListener(listener)

        val base = gridBagConstraints {
            insets = Insets(2, 0, 2, 3)
        }


        result.add(JLabel("ghc-mod executable"), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridy = 0;
        })

        result.add(ghcMod, base.setConstraints {
            gridx = 1;
            gridy = 0;
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

        result.add(JPanel(), gridBagConstraints {
            gridx = 0
            gridy = 1
            weighty = 10.0

        })

        return result
    }

    public fun getIcon(): Icon {
        return HaskellIcons.HASKELL
    }

    override fun apply() {
        HaskellSettings.getInstance().getState()!!.ghcModPath = ghcMod.getTextField()!!.getText()
        isModified = false
    }

    override fun disposeUIResources() {
    }

    override fun getHelpTopic(): String? = null

    override fun reset() {
        val modPath = HaskellSettings.getInstance().getState()?.ghcModPath ?: ""
        ghcMod.getTextField()!!.setText(modPath)
        isModified = false
    }


}
