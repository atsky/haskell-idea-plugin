package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import org.jetbrains.haskell.debugger.parser.LocalBinding

/**
 * @author Habibullin Marat
 */

public class HsDebugValue(val binding: LocalBinding): XNamedValue(binding.name ?: "<no name>") {
    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(null, binding.typeName ?: "<no type>", binding.value ?: "<no value>", false)
    }

}
