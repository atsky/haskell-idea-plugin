package org.jetbrains.haskell.parser.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType
import org.jetbrains.haskell.highlight.HaskellHighlighter
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.HaskellToken


/**
 * Created by atsky on 3/6/14.
 */

public class HaskellLexer() : LexerBase() {
    class object {
        val COMMENT_RULE: State<Boolean> = str("{-") + merge(noneOf("-"), str("-") + noneOf("}")).star() + str("-}")
        val fsm: State<IElementType> = makeFsm();

        fun makeFsm(): State<IElementType> {
            val fsm = buildFsm<IElementType> {

                add(TokenType.WHITE_SPACE,
                        oneOf(" \t").plus())

                add(TokenType.NEW_LINE_INDENT,
                        str("\n") + oneOf(" \t").star())

                add(END_OF_LINE_COMMENT,
                        str("--") + noneOf("\n").star())

                add(PRAGMA,
                        str("#") + noneOf("\n").star())


                add(COMMENT,
                        COMMENT_RULE)

                for (keyword in KEYWORDS) {
                    add(keyword, str(keyword.myName))
                }

                for (operator in OPERATORS) {
                    add(operator, str(operator.myName))
                }

                add(OPERATOR,
                        oneOf("!#$%&*+./<=>?@\\^|-~:").plus())

                add(ID,
                        oneOf('a'..'z') + oneOf(('a'..'z') + ('A'..'Z') + ('0'..'9') + '_' + '\'').star())

                add(TYPE_OR_CONS,
                        oneOf('A'..'Z') + oneOf(('a'..'z') + ('A'..'Z') + ('0'..'9') + '_' + '\'').star())

                val decimal = oneOf('0'..'9').plus()
                val octalNumber = str("0") + oneOf("oO") + oneOf('0'..'7').plus()


                add(NUMBER,
                        merge(decimal, octalNumber))


                val strSymbols = "abfnrtvx\\\"'"

                add(STRING,
                        str("\"") + merge(noneOf("\"\\"), str("\\") + oneOf(strSymbols)).star() + oneOf("\"\n"))

                add(CHARACTER,
                        str("'") + merge(noneOf("'\\"), str("\\") + oneOf(strSymbols)).star() + str("'"))

            }

            var count = 0;

            fsm trace {
                count++
            }
            System.out.println(count)

            return fsm;
        }
    }


    var myBuffer : CharSequence? = null
    var myBufferStartOffset: Int = 0
    var myOffset: Int = 0
    var myBufferEndOffset: Int = 0
    var myState: Int = 0


    var myTokenType: IElementType? = null
    var myTokenStart: Int = 0
    var myTokenEnd: Int = 0







    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.myBuffer = buffer;
        myBufferStartOffset = startOffset;
        myBufferEndOffset = endOffset;
        myOffset = startOffset;
        myState = initialState;
        advance()
    }

    override fun getState(): Int = myState

    override fun getTokenType(): IElementType? = myTokenType

    override fun getTokenStart(): Int = myTokenStart;

    override fun getTokenEnd(): Int = myTokenEnd

    override fun getBufferSequence(): CharSequence  = myBuffer!!

    override fun getBufferEnd(): Int = myBufferEndOffset;

    fun nextChar() {
        if (myOffset < getBufferEnd()) {
            myOffset++;
        }
    }

    override fun advance() {
        if (myOffset >= myBufferEndOffset) {
            myTokenType = null
            return
        }
        myTokenStart = myOffset;


        var state = fsm

        while (true) {
           val nextState = state.next(currentChar());

           if (nextState == null || myOffset >= myBufferEndOffset) {
               myTokenType = state.data
               if (myTokenType == null) {
                   myTokenType = TokenType.BAD_CHARACTER
                   nextChar()
               } else {
                   assert(myTokenStart != myOffset)
               }
               break
           } else {
               state = nextState;
           }
           nextChar()
        }

        myTokenEnd = myOffset
    }

    fun currentChar() : Char =
        if (myOffset < myBufferEndOffset) {
            myBuffer!![myOffset]
        } else {
            0.toChar()
        }


    fun skipWhitespace() {
        while (myOffset < myBufferEndOffset && Character.isWhitespace(myBuffer!![myOffset])) {
            myOffset++
        }
    }


}