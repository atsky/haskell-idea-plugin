package org.jetbrains.haskell.debugger.parser

import org.junit.Test as test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.fail
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.LinkedList
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.parser.LocalBindingList
import org.jetbrains.haskell.debugger.parser.ExpressionType
import org.jetbrains.haskell.debugger.parser.HistoryResult
import org.jetbrains.haskell.debugger.parser.HsHistoryFrameInfo

/**
 * Tests for GHCiParser class
 *
 * @author Habibullin Marat
 */
public class GHCiParserTests {
    @test fun parseSetBreakpointCommandResultColumnsRangeTest() {
        val outputWithColumnsRange = "Breakpoint 0 activated at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:3:12-13"
        var expected: BreakpointCommandResult? = BreakpointCommandResult(0, HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 3, 12, 3, 13))
        parseSetBreakpointCommandResultTest(outputWithColumnsRange, expected)
        parseSetBreakpointCommandResultTest(outputWithColumnsRange + '\n', expected)
    }

    @test fun parseSetBreakpointCommandResultPointTest() {
        val outputWithNoColumnsRange = "Breakpoint 4 activated at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:8:22"
        var expected = BreakpointCommandResult(4, HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 8, 22, 8, 22))
        parseSetBreakpointCommandResultTest(outputWithNoColumnsRange, expected)
        parseSetBreakpointCommandResultTest(outputWithNoColumnsRange + '\n', expected)
    }

    @test fun parseSetBreakpointCommandResultNotSetTest() {
        val outputNotSet = "No breakpoints found at that location."
        var expected = null
        parseSetBreakpointCommandResultTest(outputNotSet, expected)
        parseSetBreakpointCommandResultTest(outputNotSet + '\n', expected)
    }

    @test fun parseSetBreakpointCommandResultRandomTest() {
        val outputRandom = "drgj[satreihj[th\ngqrgq"
        val deq = LinkedList<String?>()
        deq.add(outputRandom)
        parseSetBreakpointCommandResultExceptionTest(deq)
    }

    @test fun parseSetBreakpointCommandResultEmptyDeqTest() =
        parseSetBreakpointCommandResultExceptionTest(LinkedList<String?>())

    private fun parseSetBreakpointCommandResultTest(input: String, expected: BreakpointCommandResult?) {
        val deq = LinkedList<String?>()
        deq.add(input)
        var actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")
    }

    private fun parseSetBreakpointCommandResultExceptionTest(deq: LinkedList<String?>) {
        try {
            GHCiParser.parseSetBreakpointCommandResult(deq)
            fail("RuntimeException is expected")
        } catch (e: RuntimeException) {}
    }

    private object TryParseStoppedAtInputs {
        val traceOutput =       "Stopped at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:4:16-47\n" +
                                "_result :: [Integer] = _\n" +
                                "a :: Integer = 8\n"
        val withPreSymbols =    ",3Stopped at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:4:37-47\n" +
                                "_result :: [a] = _\n" +
                                "right :: [a] = _\n"
        val withPreStoppedAt =  "Stopped at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:2:2-2\n" +
                                "Stopped at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:4:37-47\n" +
                                "_result :: [a] = _\n" +
                                "right :: [a] = _\n"
        val multiline =         "Stopped at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:(3,1)-(5,56)\n" +
                                "_result :: [a] = _"
        val pointStop =         "Stopped at /home/marat-x/HaskellTestSpace/Main/Main.hs:4:22\n" +
                                "_result :: a1 = _"
        val withNoInfo =        "_result :: [Integer] = _\n" +
                                "a :: Integer = 8\n" +
                                "left :: [Integer] = _\n" +
                                "right :: [Integer] = _\n"
    }

    @test fun tryParseStoppedAtTraceOutputTest() {
        var expected: HsStackFrameInfo? = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 4, 16, 4, 47),
                arrayOf(LocalBinding("a", "Integer", "8"), LocalBinding("_result", "[Integer]", "_")).toArrayList(),
                null)
        tryParseStoppedAtTest(arrayOf(TryParseStoppedAtInputs.traceOutput), expected)
    }

    @test fun tryParseStoppedAtWithPreSymbolsTest() {
        val expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 4, 37, 4, 47),
                arrayOf(LocalBinding("right", "[a]", "_"), LocalBinding("_result", "[a]", "_")).toArrayList(),
                null)
        tryParseStoppedAtTest(arrayOf(TryParseStoppedAtInputs.traceOutput, TryParseStoppedAtInputs.withPreSymbols), expected)
    }

    @test fun tryParseStoppedAtWithPreStoppedAtTest() {
        val expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 4, 37, 4, 47),
                arrayOf(LocalBinding("right", "[a]", "_"), LocalBinding("_result", "[a]", "_")).toArrayList(),
                null)
        tryParseStoppedAtTest(arrayOf(TryParseStoppedAtInputs.withPreStoppedAt), expected)
    }

    @test fun tryParseStoppedAtMultilineTest() {
        var expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 3, 1, 5, 56),
                arrayOf(LocalBinding("_result", "[a]", "_")).toArrayList(),
                null)
        tryParseStoppedAtTest(arrayOf(TryParseStoppedAtInputs.withPreStoppedAt, TryParseStoppedAtInputs.multiline), expected)
    }

    @test fun tryParseStoppedAtWithPointStopTest() {
        val expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 22, 4, 22),
                arrayOf(LocalBinding("_result", "a1", "_")).toArrayList(),
                null)
        tryParseStoppedAtTest(arrayOf(TryParseStoppedAtInputs.withPreStoppedAt,
                TryParseStoppedAtInputs.multiline,
                TryParseStoppedAtInputs.pointStop), expected)
    }

    @test fun tryParseStoppedAtWithNoInfoAtTest() =
        tryParseStoppedAtTest(arrayOf(TryParseStoppedAtInputs.withNoInfo), null)

    private fun tryParseStoppedAtTest(inputs: Array<String>, expected: HsStackFrameInfo?) {
        val deq = LinkedList<String?>()
        inputs.map { deq.addAll(it.split('\n')) }
        var actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")
    }

    @test fun parseMoveHistResultUsualTest() {
        val usual =     "Logged breakpoint at /home/marat-x/HaskellTestSpace/Main/Main.hs:3:10-14\n" +
                        "_result :: Bool\n" +
                        "n :: Integer"
        var expected: MoveHistResult? = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 3, 10, 3, 14),
                LocalBindingList(arrayOf(LocalBinding("_result", "Bool", null), LocalBinding("n", "Integer", null)).toArrayList()))
        parseMoveHistResultTest(usual, expected)
    }

    @test fun parseMoveHistResultMultilineTest() {
        val multiline = "Logged breakpoint at /home/marat-x/HaskellTestSpace/Main/Main.hs:(3,1)-(4,22)\n" +
                        "_result :: a1"
        var expected = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 3, 1, 4, 22),
                LocalBindingList(arrayOf(LocalBinding("_result", "a1", null)).toArrayList()))
        parseMoveHistResultTest(multiline, expected)
    }

    @test fun parseMoveHistResultPointTest() {
        val pointStop = "Logged breakpoint at /home/marat-x/HaskellTestSpace/Main/Main.hs:4:22\n" +
                        "_result :: a1"
        var expected = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 22, 4, 22),
                LocalBindingList(arrayOf(LocalBinding("_result", "a1", null)).toArrayList()))
        parseMoveHistResultTest(pointStop, expected)
    }

    @test fun parseMoveHistResultStoppedTest() {
        val stopped =   "Stopped at /home/marat-x/HaskellTestSpace/Main/Main.hs:10:9-16\n" +
                        "_result :: IO ()\n" +
                        "r2 :: Integer"
        var expected = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 10, 9, 10, 16),
                LocalBindingList(arrayOf(LocalBinding("_result", "IO ()", null), LocalBinding("r2", "Integer", null)).toArrayList()))
        parseMoveHistResultTest(stopped, expected)
    }

    @test fun parseMoveHistResultRandomTest() {
        val random =    "Stopperegada dfrgaargt /home/marat-x/HaskellTestSpace/Main/Main.hs:10:9-16\n" +
                        "_result ::agrg IO ()\n" +
                        "r2 :: Integer"
        var expected = null
        parseMoveHistResultTest(random, expected)
    }

    private fun parseMoveHistResultTest(input: String, expected: MoveHistResult?) {
        val deq = LinkedList<String?>()
        deq.addAll(input.split('\n'))
        var actual = GHCiParser.parseMoveHistResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.filePosition}, ${actual?.bindingList}")
    }

    @test fun parseExpressionTypeSimpleTest() {
        val simple = "a :: Bool"
        var expected: ExpressionType? = ExpressionType("a", "Bool")
        parseExpressionTypeTest(simple, expected)
    }

    @test fun parseExpressionTypeFunTypeTest() {
        val funType = "putMVar :: MVar a -> a -> IO ()"
        var expected: ExpressionType? = ExpressionType("putMVar", "MVar a -> a -> IO ()")
        parseExpressionTypeTest(funType, expected)
    }

    @test fun parseExpressionTypeFunTypeHarderTest() {
        val harderFunType = "mkWeakMVar :: MVar a -> IO () -> IO (Weak (MVar a))"
        var expected: ExpressionType? = ExpressionType("mkWeakMVar", "MVar a -> IO () -> IO (Weak (MVar a))")
        parseExpressionTypeTest(harderFunType, expected)
    }

    @test fun parseExpressionTypeWithTupleTest() {
        val withTuple = "modifyMVarMasked :: MVar a -> (a -> IO (a, b)) -> IO b"
        var expected: ExpressionType? = ExpressionType("modifyMVarMasked", "MVar a -> (a -> IO (a, b)) -> IO b")
        parseExpressionTypeTest(withTuple, expected)
    }

    @test fun parseExpressionTypeNoValGoodTest() {
        val noValGood = "_result :: _"
        var expected: ExpressionType? = ExpressionType("_result", "_")
        parseExpressionTypeTest(noValGood, expected)
    }

    @test fun parseExpressionTypeNoValBadTest() {
        val noValBad = "gsagarega :: "
        var expected: ExpressionType? = null
        parseExpressionTypeTest(noValBad, expected)
    }

    @test fun parseExpressionTypeNoNameTest() {
        val noName = " :: Bool"
        var expected: ExpressionType? = null
        parseExpressionTypeTest(noName, expected)
    }

    private fun parseExpressionTypeTest (input: String, expected: ExpressionType?) {
        val actual = GHCiParser.parseExpressionType(input)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")
    }

    private object ParseHistoryResultTestInputs {
        val frames = arrayOf(HsHistoryFrameInfo(-1, "iter", HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 10, 4, 14)),
                HsHistoryFrameInfo(-2, "iter", HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 1, 5, 30)),
                HsHistoryFrameInfo(-3, "iter", HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 22, 4, 22))
        ).toArrayList()
    }

    @test fun parseHistoryResultNormalTest() {
        val normal =    "-1  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:10-14)\n" +
                        "-2  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:(4,1)-(5,30))\n" +
                        "-3  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:22)\n" +
                        "<end of history>\n"
        var expected: HistoryResult = HistoryResult(ParseHistoryResultTestInputs.frames, true)
        parseHistoryResultTest(normal, expected)
    }

    @test fun parseHistoryResultNotFullTest() {
        val notFull =   "-1  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:10-14)\n" +
                        "-2  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:(4,1)-(5,30))\n" +
                        "-3  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:22)\n" +
                        "...\n"
        var expected: HistoryResult = HistoryResult(ParseHistoryResultTestInputs.frames, false)
        parseHistoryResultTest(notFull, expected)
    }

    private fun parseHistoryResultTest(input: String, expected: HistoryResult?) {
        val deq = LinkedList<String?>()
        deq.addAll(input.split('\n'))
        var actual = GHCiParser.parseHistoryResult(deq)
        assertEquals(expected, actual, "Actual: ${actual.frames}, ${actual.full}")
    }
}