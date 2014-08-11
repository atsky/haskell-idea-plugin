package org.jetbrains.tests.haskell.debugger

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
    test fun parseSetBreakpointCommandResultTest() {
        val outputWithColumnsRange = "Breakpoint 0 activated at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:3:12-13"
        val outputWithNoColumnsRange = "Breakpoint 4 activated at /home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs:8:22"
        val outputNotSet = "No breakpoints found at that location."
        val outputRandom = "drgj[satreihj[th\ngqrgq"
        val deq = LinkedList<String?>()

        deq.add(outputWithColumnsRange)
        var expected: BreakpointCommandResult? = BreakpointCommandResult(0, HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 3, 12, 3, 13))
        var actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")

        deq.add(outputWithColumnsRange + '\n')
        expected = BreakpointCommandResult(0, HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 3, 12, 3, 13))
        actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")

        deq.add(outputWithNoColumnsRange)
        expected = BreakpointCommandResult(4, HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 8, 22, 8, 22))
        actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")

        deq.add(outputWithNoColumnsRange + '\n')
        expected = BreakpointCommandResult(4, HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 8, 22, 8, 22))
        actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")

        deq.clear()
        deq.add(outputNotSet)
        expected = null
        actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")

        deq.add(outputNotSet + '\n')
        expected = null
        actual = GHCiParser.parseSetBreakpointCommandResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.breakpointNumber}, ${actual?.position}")

        deq.clear()
        deq.add(outputRandom)
        try {
            GHCiParser.parseSetBreakpointCommandResult(deq)
            fail("RuntimeException is expected")
        } catch (e: RuntimeException) {}

        deq.clear()
        try {
            GHCiParser.parseSetBreakpointCommandResult(deq)
            fail("RuntimeException is expected")
        } catch (e: RuntimeException) {}
    }

    test fun tryParseStoppedAtTest() {
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
        val deq = LinkedList<String?>()

        deq.addAll(traceOutput.split('\n'))
        var expected: HsStackFrameInfo? = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 4, 16, 4, 47),
                array(LocalBinding("a", "Integer", "8"), LocalBinding("_result", "[Integer]", "_")).toArrayList(),
                null)
        var actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")

        deq.addAll(withPreSymbols.split('\n'))
        expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 4, 37, 4, 47),
                array(LocalBinding("right", "[a]", "_"), LocalBinding("_result", "[a]", "_")).toArrayList(),
                null)
        actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")

        deq.clear()
        deq.addAll(withPreStoppedAt.split('\n'))
        actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")

        deq.addAll(multiline.split('\n'))
        expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/IdeaProjects/HaskellTestModule/src/Mine.hs", 3, 1, 5, 56),
                array(LocalBinding("_result", "[a]", "_")).toArrayList(),
                null)
        actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")

        deq.addAll(pointStop.split('\n'))
        expected = HsStackFrameInfo(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 22, 4, 22),
                array(LocalBinding("_result", "a1", "_")).toArrayList(),
                null)
        actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")

        deq.clear()
        deq.addAll(withNoInfo.split('\n'))
        expected = null
        actual = GHCiParser.tryParseStoppedAt(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.bindings}, ${actual?.functionName}")
    }

    test fun parseMoveHistResultTest() {
        val usual =     "Logged breakpoint at /home/marat-x/HaskellTestSpace/Main/Main.hs:3:10-14\n" +
                        "_result :: Bool\n" +
                        "n :: Integer"
        val multiline = "Logged breakpoint at /home/marat-x/HaskellTestSpace/Main/Main.hs:(3,1)-(4,22)\n" +
                        "_result :: a1"
        val pointStop = "Logged breakpoint at /home/marat-x/HaskellTestSpace/Main/Main.hs:4:22\n" +
                        "_result :: a1"
        val stopped =   "Stopped at /home/marat-x/HaskellTestSpace/Main/Main.hs:10:9-16\n" +
                        "_result :: IO ()\n" +
                        "r2 :: Integer"
        val random =    "Stopperegada dfrgaargt /home/marat-x/HaskellTestSpace/Main/Main.hs:10:9-16\n" +
                        "_result ::agrg IO ()\n" +
                        "r2 :: Integer"
        val deq = LinkedList<String?>()

        deq.addAll(usual.split('\n'))
        var expected: MoveHistResult? = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 3, 10, 3, 14),
                LocalBindingList(array(LocalBinding("_result", "Bool", null), LocalBinding("n", "Integer", null)).toArrayList()))
        var actual = GHCiParser.parseMoveHistResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.filePosition}, ${actual?.bindingList}")

        deq.clear()
        deq.addAll(multiline.split('\n'))
        expected = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 3, 1, 4, 22),
                LocalBindingList(array(LocalBinding("_result", "a1", null)).toArrayList()))
        actual = GHCiParser.parseMoveHistResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.filePosition}, ${actual?.bindingList}")

        deq.clear()
        deq.addAll(pointStop.split('\n'))
        expected = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 22, 4, 22),
                LocalBindingList(array(LocalBinding("_result", "a1", null)).toArrayList()))
        actual = GHCiParser.parseMoveHistResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.filePosition}, ${actual?.bindingList}")

        deq.clear()
        deq.addAll(stopped.split('\n'))
        expected = MoveHistResult(
                HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 10, 9, 10, 16),
                LocalBindingList(array(LocalBinding("_result", "IO ()", null), LocalBinding("r2", "Integer", null)).toArrayList()))
        actual = GHCiParser.parseMoveHistResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.filePosition}, ${actual?.bindingList}")

        deq.clear()
        deq.addAll(random.split('\n'))
        expected = null
        actual = GHCiParser.parseMoveHistResult(deq)
        assertEquals(expected, actual, "Actual: ${actual?.filePosition}, ${actual?.filePosition}, ${actual?.bindingList}")
    }

    test fun parseExpressionTypeTest() {
        val usual0 = "a :: Bool"
        val usual1 = "putMVar :: MVar a -> a -> IO ()"
        val usual2 = "mkWeakMVar :: MVar a -> IO () -> IO (Weak (MVar a))"
        val usual3 = "modifyMVarMasked :: MVar a -> (a -> IO (a, b)) -> IO b"
        val noValGood = "_result :: _"
        val noValBad = "gsagarega :: "
        val noName = " :: Bool"

        var expected: ExpressionType? = ExpressionType("a", "Bool")
        var actual = GHCiParser.parseExpressionType(usual0)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")

        expected: ExpressionType? = ExpressionType("putMVar", "MVar a -> a -> IO ()")
        actual = GHCiParser.parseExpressionType(usual1)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")

        expected: ExpressionType? = ExpressionType("mkWeakMVar", "MVar a -> IO () -> IO (Weak (MVar a))")
        actual = GHCiParser.parseExpressionType(usual2)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")

        expected: ExpressionType? = ExpressionType("modifyMVarMasked", "MVar a -> (a -> IO (a, b)) -> IO b")
        actual = GHCiParser.parseExpressionType(usual3)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")

        expected: ExpressionType? = ExpressionType("_result", "_")
        actual = GHCiParser.parseExpressionType(noValGood)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")

        expected: ExpressionType? = null
        actual = GHCiParser.parseExpressionType(noValBad)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")

        expected: ExpressionType? = null
        actual = GHCiParser.parseExpressionType(noName)
        assertEquals(expected, actual, "Actual: ${actual?.expression}, ${actual?.expressionType}")
    }

    test fun parseHistoryResultTest() {
        val usual =     "-1  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:10-14)\n" +
                        "-2  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:(4,1)-(5,30))\n" +
                        "-3  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:22)\n" +
                        "<end of history>\n"

        val notFull =   "-1  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:10-14)\n" +
                        "-2  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:(4,1)-(5,30))\n" +
                        "-3  : iter (/home/marat-x/HaskellTestSpace/Main/Main.hs:4:22)\n" +
                        "...\n"
        val deq = LinkedList<String?>()

        deq.addAll(usual.split('\n'))
        var frames = array(HsHistoryFrameInfo(-1, "iter", HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 10, 4, 14)),
                HsHistoryFrameInfo(-2, "iter", HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 1, 5, 30)),
                HsHistoryFrameInfo(-3, "iter", HsFilePosition("/home/marat-x/HaskellTestSpace/Main/Main.hs", 4, 22, 4, 22)))
        var expected: HistoryResult = HistoryResult(frames.toArrayList(), true)
        var actual = GHCiParser.parseHistoryResult(deq)
        assertEquals(expected, actual, "Actual: ${actual.frames}, ${actual.full}")

        deq.clear()
        deq.addAll(notFull.split('\n'))
        expected: HistoryResult = HistoryResult(frames.toArrayList(), false)
        actual = GHCiParser.parseHistoryResult(deq)
        assertEquals(expected, actual, "Actual: ${actual.frames}, ${actual.full}")
    }
}