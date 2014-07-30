package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import com.intellij.psi.PsiElement

public class SourceRepo(node: ASTNode) : Section(node) {

    public override fun getRequiredFieldNames(): List<String> = listOf("type", "location")

    public override fun getAvailableFieldNames(): List<String> {
        return REPO_SOURCE_FIELDS
    }

    public override fun allRequiredFieldsExist(): String? {
        val nodes = getSectChildren()

        var typeValue: PropertyValue? = null
        var locationFlag = false
        var moduleFlag = false
        var tagFlag = false

        for (node in nodes) {
            when (node) {
                is TypeField         -> typeValue = node.getLastValue()
                is RepoLocationField -> locationFlag = true
                is RepoModuleField   -> moduleFlag = true
                is RepoTagField      -> tagFlag = true
            }
        }
        if (typeValue == null) return "type field is required"
        if (!locationFlag)     return "location field is required"
        if ((typeValue!!.getText() == "cvs") && !moduleFlag) {
            return "module field is required with CVS repository type"
        }
        if (getRepoKind().equals("this") && !tagFlag) return "tag field is required when repository kind is \"this\""
        return null
    }

    public fun getRepoKind(): String = getAfterTypeNode()!!.getText()!!
}
