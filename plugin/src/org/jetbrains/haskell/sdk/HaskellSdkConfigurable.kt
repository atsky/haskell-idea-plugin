package org.jetbrains.haskell.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.SdkModificator
import javax.swing.*

public class HaskellSdkConfigurable() : AdditionalDataConfigurable {
    private val myForm: HaskellSdkConfigurableForm = HaskellSdkConfigurableForm()

    private var mySdk: Sdk? = null

    override fun setSdk(sdk: Sdk?) {
        mySdk = sdk
    }

    override fun createComponent(): JComponent {
        return myForm.getContentPanel()
    }

    override fun isModified(): Boolean {
        return myForm.isModified
    }

    override fun apply() {
        val newData = HaskellSdkAdditionalData(myForm.getCabalPath(), myForm.getCabalLibPath())

        val modificator = mySdk!!.getSdkModificator()
        modificator.setSdkAdditionalData(newData)
        ApplicationManager.getApplication()!!.runWriteAction(object : Runnable {
            override fun run() {
                modificator.commitChanges()
            }
        })
        myForm.isModified = false
    }

    override fun reset() {
        val data = mySdk!!.getSdkAdditionalData()
        val ghcData: HaskellSdkAdditionalData?
        if (data != null) {
            if (!(data is HaskellSdkAdditionalData)) {
                return
            }
            ghcData = (data as HaskellSdkAdditionalData)
            val cabalPath = ghcData?.getCabalPath() ?: ""
            val cabalLibPath = ghcData?.getCabalDataPath() ?: ""

            myForm.init(cabalPath, cabalLibPath)
        } else {
            myForm.init(HaskellSdkAdditionalData.getDefaultCabalPath(),
                        HaskellSdkAdditionalData.getDefaultCabalDataPath())
        }


        myForm.isModified = false
    }

    override fun disposeUIResources() {
    }

}
