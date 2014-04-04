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
        checkFsm(str("a") + str("b")) {
            accepts("ab")
            notAccepts("a")
        }
    }



    Test
    public fun testNot() {
        checkFsm(not(str("lol"))) {
            accepts("trol")
            accepts("lo")
            notAccepts("lol")
        }
    }

    Test
    public fun testMerge2() {
        checkFsm(merge(noneOf("-"), str("-") + noneOf("}")) + str("-}")) {
            accepts("-a-}")
            notAccepts("-a")
        }
    }

    Test
    public fun testLoop() {
        checkFsm(not(anything() + str("lo") + anything())) {
            accepts("trol")
            notAccepts("ololo")
        }
    }

    Test
    public fun testComment() {
        checkFsm(str("--") + noneOf("\n").star()) {
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

    Test
    public fun testCommentMerger() {
        checkFsm(merge(str("--") + noneOf("\n").star(), HaskellLexer.COMMENT_RULE)) {
            accepts("--test")
            notAccepts("--test\n")
            notAccepts("{-")
            notAccepts("")
            accepts("{-aaa-}")
        }
    }

    Test
    public fun testIndent() {
        checkFsm(str("\n") + oneOf(" \t\r").star()) {
            accepts("\n   ")
            notAccepts("    \n")
        }
    }

    Test
    public fun testAnything() {
        checkFsm(anything()) {
            accepts("any")
        }
    }
}