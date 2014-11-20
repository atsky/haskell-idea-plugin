package org.jetbrains.grammar.dumb

import java.util.ArrayList

/**
 * Created by atsky on 11/20/14.
 */
class RuleCache {
    public var started : Boolean = false;
    public var trees : MutableList<NonTerminalTree>? = null;
    public val states : MutableList<ParserState> = ArrayList();
}