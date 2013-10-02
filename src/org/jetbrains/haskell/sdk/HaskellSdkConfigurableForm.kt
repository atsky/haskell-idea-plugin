package org.jetbrains.haskell.sdk

import javax.swing.*
import java.awt.GridBagLayout
import org.jetbrains.haskell.util.*

public open class HaskellSdkConfigurableForm() {
    private var myModified : Boolean = false
    private var myCabalPath: JTextField = JTextField()
    private var myGhcOptions: JTextField = JTextField()

    public open fun getContentPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        panel.add(JLabel("cabal path"), gridBagConstraints {
            gridx = 0;
            gridy = 0;
        })
        panel.add(myCabalPath, gridBagConstraints {
            gridx = 1;
            gridy = 0;
        })
        panel.add(JPanel(), gridBagConstraints {
            gridx = 2;
            gridy = 0;
            weightx = 1.0

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
