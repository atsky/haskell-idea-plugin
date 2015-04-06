package org.jetbrains.haskell.repl;

import com.intellij.execution.console.ConsoleRootType;

/**
 * Created by atsky on 06/04/15.
 */
public class HaskellConsoleRootType extends ConsoleRootType {
    protected HaskellConsoleRootType() {
        super("haskell", "REPL");
    }
}
