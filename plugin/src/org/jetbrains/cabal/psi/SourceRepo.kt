package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.psi.PsiElement
import java.util.ArrayList

public class SourceRepo(node: ASTNode) : Section(node) {

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(SOURCE_REPO_FIELDS.keySet())
        return res
    }

    public override fun check(): List<ErrorMessage> {
        val res = ArrayList<ErrorMessage>()

        val typeField   = getField(javaClass<TypeField>())
        val locationField = getField(javaClass<RepoLocationField>())
        val moduleField = getField(javaClass<RepoModuleField>())
        val tagField = getField(javaClass<RepoTagField>())

        if (typeField == null)     res.add(ErrorMessage(getSectTypeNode(), "type field is required", "error"))
        if (locationField == null) res.add(ErrorMessage(getSectTypeNode(), "location field is required", "error"))
        if ((typeField?.getValue()?.getText() == "cvs") && (moduleField == null)) {
            res.add(ErrorMessage(getSectTypeNode(), "module field is required with CVS repository type", "error"))
        }
        if ((typeField?.getValue()?.getText() != "cvs") && (moduleField != null)) {
            res.add(ErrorMessage(moduleField.getKeyNode(), "module field is disallowed when repository type isn't CVS", "error"))
        }
        if (isKind("this") && (tagField == null)) res.add(ErrorMessage(getSectTypeNode(), "tag field is required when repository kind is \"this\"", "error"))
        return res
    }

    public fun getRepoKinds(): List<String> {
        var node = getFirstChild()
        var res = ArrayList<String>()
        while ((node != null) && (node !is RepoKind)) {
            node = node!!.getNextSibling()
        }
        while (node is RepoKind) {
            res.add((node as RepoKind).getText()!!)
            node = node!!.getNextSibling()
        }
        return res
    }

    public fun isKind(kindName: String): Boolean = kindName in getRepoKinds()
}
