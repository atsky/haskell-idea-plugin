package org.jetbrains.haskell.sdk

import javax.swing.*
import java.awt.GridBagLayout
import org.jetbrains.haskell.util.*
import java.awt.GridBagConstraints
import java.awt.Insets
import com.intellij.ui.DocumentAdapter
import javax.swing.event.DocumentEvent
import com.intellij.openapi.ui.TextFieldWithBrowseButton

public open class HaskellSdkConfigurableForm() {
    public var isModified: Boolean = false
    private val cabalPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val cabalLibPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    public open fun getContentPanel(): JComponent {
        val panel = JPanel(GridBagLayout())

        val base = gridBagConstraints {
            insets = Insets(2, 0, 2, 3)
        }

        val listener : DocumentAdapter = object : DocumentAdapter() {

            override fun textChanged(e: DocumentEvent?) {
                isModified = true;
            }

        };

        cabalPathField.getTextField()!!.getDocument()!!.addDocumentListener(listener)
        cabalLibPathField.getTextField()!!.getDocument()!!.addDocumentListener(listener)

        panel.add(JLabel("Cabal executable path"), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridy = 0;
        })

        panel.add(cabalPathField, base.setConstraints {
            gridx = 1;
            gridy = 0;
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

        panel.add(Box.createHorizontalStrut(1), base.setConstraints {
            gridx = 2;
            gridy = 0;
            weightx = 0.1
        })

        panel.add(JLabel("Cabal data path"), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridy = 1;
        })

        panel.add(cabalLibPathField, base.setConstraints {
            gridx = 1;
            gridy = 1;
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

        panel.add(Box.createHorizontalStrut(1), base.setConstraints {
            gridx = 2;
            gridy = 1;
            weightx = 0.1
        })


        return panel

    }

    public open fun getCabalPath(): String {
        return cabalPathField.getText()!!
    }
    public open fun getCabalLibPath(): String {
        return cabalLibPathField.getText()!!
    }

    public open fun init(cabalPath : String, cabalLibPath : String): Unit {
        this.cabalPathField.setText(cabalPath)
        this.cabalLibPathField.setText(cabalLibPath)
    }


}
