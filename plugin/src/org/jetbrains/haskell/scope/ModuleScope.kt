package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.ValueDeclaration
import org.jetbrains.haskell.psi.Module
import java.util.ArrayList
import org.jetbrains.haskell.psi.DataDeclaration
import org.jetbrains.haskell.psi.ConstructorDeclaration
import org.jetbrains.haskell.psi.TypeName

/**
 * Created by atsky on 03/05/14.
 */
public class ModuleScope(val module : Module) {


    fun getDeclaredValues() : List<ValueDeclaration> {
        val list = ArrayList(module.getValueDeclarationList())

        list.addAll(module.getClassDeclarationList().flatMap { it.getValueDeclarationList() })
        return list
    }

    fun getVisibleValues() : List<ValueDeclaration> {
        val list = ArrayList(getDeclaredValues())

        list.addAll(module.getImportList().flatMap { ImportScope(it).getValues() })

        return list

    }

    fun getVisibleTypes() : List<TypeName> {
        val list = ArrayList(module.getTypeDeclarationList())

        list.addAll(module.getImportList().flatMap { ImportScope(it).getTypeDeclarations() })

        val result = ArrayList<TypeName>();
        result.addAll(list.map { it.getTypeName() }.filterNotNull())
        result.addAll(getVisibleDataDeclarations().map { it.getTypeName() }.filterNotNull())
        return result

    }


    fun getVisibleDataDeclarations() : List<DataDeclaration> {
        val list = ArrayList(module.getDataDeclarationList())

        list.addAll(module.getImportList().flatMap { ImportScope(it).getDataDeclarations() })

        return list

    }

    fun getVisibleConstructors() : List<ConstructorDeclaration> {
        return getVisibleDataDeclarations().flatMap { it.getConstructorDeclarationList() }
    }
}
