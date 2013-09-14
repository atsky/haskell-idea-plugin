package org.jetbrains.haskell.util.lisp

import java.util.ArrayList


public open class LispParser(val myText: String) {
    private var myIndex: Int = 0
    private var myTokenStart: Int = 0

    private open fun ch(): Char {
        if (myIndex < myText.length()) {
            return myText.charAt(myIndex)
        } else {
            return 0.toChar()
        }
    }

    public open fun parseExpression(): SExpression {
        skipWhitespace()
        if (ch() == '\'') {
            myIndex++
        }

        if (ch() == '(') {
            myIndex++
            val result = ArrayList<SExpression>()
            while (ch() != ')') {
                result.add(parseExpression())
                skipWhitespace()
            }
            myIndex++
            return SList(result)
        }
        else
            if (ch() == '"') {
                myIndex++
                startToken()
                while (ch() != '"') {
                    if (ch() == '\\') {
                        myIndex++
                    }

                    myIndex++
                }
                val expression = SAtom(getToken().replace("\\\"", "\""))
                myIndex++
                return expression
            } else {
                startToken()
                while (!Character.isWhitespace(ch()) && ch() != 0.toChar() && ch() != ')')
                {
                    myIndex++
                }
                return SAtom(getToken())
            }
    }

    private open fun startToken(): Unit {
        myTokenStart = myIndex
    }

    private open fun skipWhitespace(): Unit {
        while (Character.isWhitespace(ch())) {
            myIndex++
        }
    }

    public open fun getToken(): String {
        return myText.substring(myTokenStart, myIndex)
    }


}
