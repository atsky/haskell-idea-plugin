package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Import
import org.jetbrains.haskell.psi.SignatureDeclaration
import org.jetbrains.haskell.psi.Declaration
import org.jetbrains.haskell.psi.DataDeclaration
import com.intellij.psi.util.PsiTreeUtil
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration

public class ImportScope(val import : Import) {


    public fun filterDeclarations<A : Declaration>(list : List<A>) : List<A> {
        val moduleExports = import.getModuleExports()
        if (moduleExports != null) {
            if (import.hasHiding()) {

            } else {

            }
        }
        return list
    }

    fun getValues() : List<SignatureDeclaration> {
        val module = import.findModule()
        if (module != null) {
            //return filterDeclarations(ModuleScope(module).getSignatureDeclaration())
        }
        return listOf()
    }

    fun getDataDeclarations() : List<DataDeclaration> {
        val module = import.findModule()
        if (module != null) {
            return filterDeclarations(module.getDataDeclarationList())
        }
        return listOf()
    }

    fun getTypeDeclarations() : List<TypeDeclaration> {
        val module = import.findModule()
        if (module != null) {
            //return filterDeclarations(module.getTypeDeclarationList())
        }
        return listOf()
    }

}