package org.jetbrains.haskell.parser.lexer

import junit.framework.TestCase
import org.jetbrains.haskell.parser.*

import org.junit.Assert
import org.junit.Test


/**
 * Created by atsky on 3/11/14.
 */
public class LexerTest : TestCase() {

    class FsmChecker<A>(val fsm : State<A>) {

        fun <A : Any>runFsm(fsm : State<A>, str : String) : A? {
            var state : State<A>? = fsm
            for (ch in str) {
                val current = state;
                if (current != null) {
                    state = current.next(ch)
                }
            }
            return state?.data
        }

        fun accepts(str : String) {
            Assert.assertNotNull(runFsm(fsm, str))
        }

        fun notAccepts(str : String) {
            Assert.assertNull(runFsm(fsm, str))
        }

    }

    fun <A>checkFsm(fsm : State<A>, scenario : FsmChecker<A>.()->Unit) {
        FsmChecker<A>(fsm).scenario()
    }

    Test
    public fun testMerge() {
        checkFsm(fromStr("a") + fromStr("b")) {
            accepts("ab")
            notAccepts("a")
        }
    }



    Test
    public fun testNot() {
        checkFsm(not(fromStr("lol"))) {
            accepts("trol")
            accepts("lo")
            notAccepts("lol")
        }
    }

    Test
    public fun testMerge2() {
        checkFsm(merge(noneOf("-"), fromStr("-") + noneOf("}")) + fromStr("-}")) {
            accepts("-a-}")
            notAccepts("-a")
        }
    }

    Test
    public fun testLoop() {
        checkFsm(not(anything() + fromStr("lo") + anything())) {
            accepts("trol")
            notAccepts("ololo")
        }
    }

    Test
    public fun testComment() {
        checkFsm(fromStr("--") + noneOfStar("\n").loop()) {
            accepts("--test")
            notAccepts("--test\n")
        }
    }

    Test
    public fun testComment2() {
        checkFsm(HaskellLexer.COMMENT_RULE) {
            notAccepts("{-")
            notAccepts("")
            accepts("{-aaa-}")
        }
    }

    public fun testCommentMerger() {
        checkFsm(merge(fromStr("--") + noneOfStar("\n").loop(), HaskellLexer.COMMENT_RULE)) {
            accepts("--test")
            notAccepts("--test\n")
            notAccepts("{-")
            notAccepts("")
            accepts("{-aaa-}")
        }


    }

    Test
    public fun testAnything() {
        checkFsm(anything()) {
            accepts("any")
        }
    }
}