package org.jetbrains.haskell.sdk

import javax.swing.*
import java.awt.GridBagLayout
import org.jetbrains.haskell.util.*
import java.awt.GridBagConstraints
import java.awt.Insets
import com.intellij.ui.DocumentAdapter
import javax.swing.event.DocumentEvent

public open class HaskellSdkConfigurableForm() {
    private var myModified : Boolean = false
    private var myCabalPath: JTextField = JTextField()
    private var myGhcOptions: JTextField = JTextField()

    public open fun getContentPanel(): JComponent {
        val panel = JPanel(GridBagLayout())

        val base = gridBagConstraints {
            insets = Insets(2, 0, 2, 3)
        }

        val listener : DocumentAdapter = object : DocumentAdapter() {

            override fun textChanged(e: DocumentEvent?) {
                myModified = true;
            }

        };

        myCabalPath.getDocument()!!.addDocumentListener(listener)
        myGhcOptions.getDocument()!!.addDocumentListener(listener)

        panel.add(JLabel("Cabal path:"), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridy = 0;
        })

        panel.add(myCabalPath, base.setConstraints {
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

        panel.add(JLabel("Ghc options:"), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridy = 1;
        })

        panel.add(myGhcOptions, base.setConstraints {
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
    public open fun isModified(): Boolean {
        return myModified
    }
    public open fun getCabalPath(): String {
        return myCabalPath.getText()!!
    }
    public open fun getGhcOptions(): String {
        return myGhcOptions.getText()!!
    }
    public open fun setModified(modified: Boolean): Unit {
        this.myModified = modified
    }
    public open fun init(ghcOptions: String, cabalPath : String): Unit {
        myGhcOptions.setText(ghcOptions)
        myCabalPath.setText(cabalPath)
    }


}
