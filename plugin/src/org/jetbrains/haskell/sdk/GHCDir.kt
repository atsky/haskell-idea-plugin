package org.jetbrains.haskell.sdk


import org.jetbrains.haskell.util.GHCUtil
import org.jetbrains.haskell.util.GHCVersion

class GHCDir(val name: String) {
    val version: GHCVersion = GHCUtil.getVersion(name)
}
