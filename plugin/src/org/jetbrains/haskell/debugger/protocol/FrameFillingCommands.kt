package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.LocalBindingList
import org.jetbrains.haskell.debugger.parser.LocalBinding

/**
 * @author Habibullin Marat
 */
// todo: refactor
public class SequenceOfBacksCommand(val allHistFramesArray: ArrayList<HsCommonStackFrameInfo>,
                                    val syncObject: Lock,
                                    val frameBindingsAreSet: Condition,
                                    val sequenceLength: Int,
                                    var currentStep: Int = sequenceLength - 1,
                                    val debugProcess: HaskellDebugProcess) : RealTimeCommand(
        SequenceOfBacksCommand.StandardSequenceOfBacksCallback(
                allHistFramesArray,
                syncObject,
                frameBindingsAreSet,
                sequenceLength,
                currentStep,
                debugProcess
        )
) {
    override fun getBytes(): ByteArray = ":back\n".toByteArray()
    override fun parseOutput(output: Deque<String?>): ParseResult? = Parser.tryParseLocalBindings(output)

    class object {
        public class StandardSequenceOfBacksCallback(val allHistFramesArray: ArrayList<HsCommonStackFrameInfo>,
                                                     val syncObject: Lock,
                                                     val frameBindingsAreSet: Condition,
                                                     val sequenceLength: Int,
                                                     var currentStep: Int = sequenceLength - 1,
                                                     val debugProcess: HaskellDebugProcess) : CommandCallback() {
            override fun execAfterParsing(result: ParseResult?) {
                if (result != null && result is LocalBindingList) {
                    if (currentStep != 0) {
                        fillFrameBindingsIfNeeded(result.list)
                        currentStep -= 1
                        debugProcess.debugger.backsSequence(SequenceOfBacksCommand(
                                allHistFramesArray,
                                syncObject,
                                frameBindingsAreSet,
                                sequenceLength,
                                currentStep,
                                debugProcess
                        ))
                    } else {
                        fillLastFrameBindings(result.list)
                        debugProcess.debugger.forwardsSequence(SequenceOfForwardsCommand(
                                frameBindingsAreSet,
                                syncObject,
                                sequenceLength,
                                debugProcess = debugProcess)
                        )
                    }
                }
            }

            private fun fillFrameBindingsIfNeeded(list: ArrayList<LocalBinding>) {
                val index = currentFrameIndexInFramesArray()
                if (allHistFramesArray.get(index).bindings == null) {
                    allHistFramesArray.get(index).bindings = list
                }
            }

            private fun fillLastFrameBindings(list: ArrayList<LocalBinding>) {
                syncObject.lock()
                val index = currentFrameIndexInFramesArray()
                allHistFramesArray.get(index).bindings = list
                syncObject.unlock()
            }

            private fun currentFrameIndexInFramesArray() = sequenceLength - currentStep - 1
        }
    }
}

public class SequenceOfForwardsCommand(val frameBindingsAreSet: Condition,
                                       val syncObject: Lock,
                                       val sequenceLength: Int,
                                       var currentStep: Int = 1,
                                       val debugProcess: HaskellDebugProcess) : RealTimeCommand(
        SequenceOfForwardsCommand.StandardSequenceOfForwardsCallback(
                frameBindingsAreSet,
                syncObject,
                sequenceLength,
                currentStep,
                debugProcess
        )
) {
    override fun getBytes(): ByteArray = ":forward\n".toByteArray()

    override fun parseOutput(output: Deque<String?>): ParseResult? = null

    class object {
        public class StandardSequenceOfForwardsCallback(val frameBindingsAreSet: Condition,
                                                        val syncObject: Lock,
                                                        val sequenceLength: Int,
                                                        var currentStep: Int = 1,
                                                        val debugProcess: HaskellDebugProcess) : CommandCallback() {
            override fun execAfterParsing(result: ParseResult?) {
                if (currentStep != sequenceLength) {
                    currentStep += 1
                    debugProcess.debugger.forwardsSequence(SequenceOfForwardsCommand(
                            frameBindingsAreSet,
                            syncObject,
                            sequenceLength,
                            currentStep,
                            debugProcess
                    ))
                } else {
                    syncObject.lock()
                    try {
                        frameBindingsAreSet.signal()
                    } finally {
                        syncObject.unlock()
                    }
                }
            }
        }
    }
}