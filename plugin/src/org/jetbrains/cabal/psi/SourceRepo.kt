package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.psi.PsiElement
import java.util.ArrayList

class SourceRepo(node: ASTNode) : Section(node) {

    override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(SOURCE_REPO_FIELDS.keys)
        return res
    }

    override fun check(): List<ErrorMessage> {
        val res = ArrayList<ErrorMessage>()

        val typeField   = getField(TypeField::class.java)
        val locationField = getField(RepoLocationField::class.java)
        val moduleField = getField(RepoModuleField::class.java)
        val tagField = getField(RepoTagField::class.java)

        if (typeField == null)     res.add(ErrorMessage(getSectTypeNode(), "type field is required", "error"))
        if (locationField == null) res.add(ErrorMessage(getSectTypeNode(), "location field is required", "error"))
        if ((typeField?.getValue()?.text == "cvs") && (moduleField == null)) {
            res.add(ErrorMessage(getSectTypeNode(), "module field is required with CVS repository type", "error"))
        }
        if ((typeField?.getValue()?.text != "cvs") && (moduleField != null)) {
            res.add(ErrorMessage(moduleField.getKeyNode(), "module field is disallowed when repository type isn't CVS", "error"))
        }
        if (isKind("this") && (tagField == null)) res.add(ErrorMessage(getSectTypeNode(), "tag field is required when repository kind is \"this\"", "error"))
        return res
    }

    fun getRepoKinds(): List<String> {
        var node = firstChild
        var res = ArrayList<String>()
        while ((node != null) && (node !is RepoKind)) {
            node = node.nextSibling
        }
        while (node is RepoKind) {
            res.add(node.getText()!!)
            node = node.getNextSibling()
        }
        return res
    }

    fun isKind(kindName: String): Boolean = kindName in getRepoKinds()
}
