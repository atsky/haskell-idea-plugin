package org.jetbrains.haskell.findUsages

import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import com.intellij.psi.tree.TokenSet
import org.jetbrains.grammar.HaskellLexerTokens
import org.jetbrains.haskell.parser.token.COMMENTS

/**
 * Created by atsky on 13/02/15.
 */
class HaskellWordsScanner : DefaultWordsScanner(
        HaskellLexer(),
        TokenSet.create(
                HaskellLexerTokens.VARID,
                HaskellLexerTokens.VARSYM,
                HaskellLexerTokens.CONID,
                HaskellLexerTokens.CONSYM),
        COMMENTS,
        TokenSet.create(HaskellLexerTokens.STRING))