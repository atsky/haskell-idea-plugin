package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import com.intellij.psi.PsiElement

public class SourceRepo(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public override val REQUIRED_FIELD_NAMES: List<String>? = listOf(
            "type",
            "location"
    )

    public override fun getAvailableFieldNames(): List<String> {
        return REPO_SOURCE_FIELDS
    }

    public override fun allRequiredFieldsExist(): String? {
        val nodes = getSectChildren()

        var typeValue: String? = null
        var locationFlag = false
        var moduleFlag = false
        var tagFlag = false

        for (node in nodes) {
            if (node !is Field) continue
            val fieldNode = node as Field
            when (fieldNode.getFieldName()) {
                "type" -> typeValue = fieldNode.getLastValue()
                "location" -> locationFlag = true
                "module" -> moduleFlag = true
                "tag" -> tagFlag = true
            }
        }
        if (typeValue == null) return "type field is required"
        if (!locationFlag)     return "location field is required"
        if ((typeValue == "cvs") && !moduleFlag) {
            return "module field is required with CVS repository type"
        }
        if (getAfterTypeInfo().equals("this") && !tagFlag) return "tag field is required when repository kind is \"this\""
        return null
    }

    public fun getRepoType(): String? {
        val nodes = getSectChildren()
        for (node in nodes) {
            if ((node is Field) && node.hasName("type")) {
                return node.getLastValue()
            }
        }
        return null
    }
}
