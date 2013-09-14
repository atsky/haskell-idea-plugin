package org.jetbrains.haskell.sdk;


import org.jetbrains.haskell.util.GHCUtil;
import org.jetbrains.haskell.util.GHCVersion;

final class GHCDir {

    final String name;
    final GHCVersion version;

    GHCDir(String name) {
        this.name = name;
        this.version = GHCUtil.getVersion(name);
    }
}
