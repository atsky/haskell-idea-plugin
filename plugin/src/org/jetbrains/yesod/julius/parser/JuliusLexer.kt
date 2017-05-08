package org.jetbrains.yesod.julius.parser

/**
 * @author Leyla H
 */

import com.intellij.lexer.FlexAdapter
import java.io.Reader

class JuliusLexer : FlexAdapter(_JuliusLexer(null as Reader?))
