package org.jetbrains.haskell.parser.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.token.HaskellTokenTypes
import com.intellij.psi.TokenType
import org.jetbrains.haskell.highlight.HaskellHighlighter
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.HaskellToken


/**
 * Created by atsky on 3/6/14.
 */

public class HaskellLexer() : LexerBase(), HaskellTokenTypes {
    val operators = listOf<HaskellToken>(
            COMMA,
            DOT,
            SEMICOLON,
            LEFT_PAREN,
            RIGHT_PAREN)

    var myBuffer : CharSequence? = null
    var myBufferStartOffset: Int = 0
    var myOffset: Int = 0
    var myBufferEndOffset: Int = 0
    var myState: Int = 0


    var myTokenType: IElementType? = null
    var myTokenStart: Int = 0
    var myTokenEnd: Int = 0

    val fsm: State<IElementType> = makeFsm();

    fun makeFsm() : State<IElementType> {
        val fsm = buildFsm<IElementType> {
            add(TokenType.WHITE_SPACE,
                    anyOf(" \t\n\r").many())

            add(END_OF_LINE_COMMENT,
                    fromStr("--") + noneOfStar("\n").loop())

            add(PRAGMA,
                    fromStr("#") + noneOfStar("\n").loop())


            add(COMMENT,
                    COMMENT_RULE)

            for (keyword in KEYWORDS) {
                add(keyword, fromStr(keyword.myName!!))
            }

            for (operator in operators) {
                add(operator, fromStr(operator.myName!!))
            }

            add(ID,
                    anyOf('a'..'z') + anyOf(('a'..'z') + ('A'..'Z') + ('0'..'9') + '_').loop())

            add(TYPE_CONS,
                    anyOf('A'..'Z') + anyOf(('a'..'z') + ('A'..'Z') + ('0'..'9') + '_').loop())

        }

        var count = 0;

        fsm trace {
            count++
        }
        System.out.println(count)

        return fsm;
    }


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
        myOffset++;
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

    class object {
        val COMMENT_RULE : State<Boolean> = fromStr("{-") + merge(noneOf("-"), fromStr("-") + noneOf("}")).loop() + fromStr("-}")
    }
}