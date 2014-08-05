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
                                    val debugProcess: HaskellDebugProcess)
: RealTimeCommand<LocalBindingList?>(SequenceOfBacksCommand.StandardSequenceOfBacksCallback(
                                    allHistFramesArray,
                                    syncObject,
                                    frameBindingsAreSet,
                                    sequenceLength,
                                    currentStep,
                                    debugProcess)) {
    override fun getText(): String = ":back\n"
    override fun parseGHCiOutput(output: Deque<String?>): LocalBindingList? = Parser.tryParseLocalBindings(output)

    class object {
        private class StandardSequenceOfBacksCallback(val allHistFramesArray: ArrayList<HsCommonStackFrameInfo>,
                                                      val syncObject: Lock,
                                                      val frameBindingsAreSet: Condition,
                                                      val sequenceLength: Int,
                                                      var currentStep: Int = sequenceLength - 1,
                                                      val debugProcess: HaskellDebugProcess)
                                                      : CommandCallback<LocalBindingList?>() {
            override fun execAfterParsing(result: LocalBindingList?) {
                if (result != null) {
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
                                       val debugProcess: HaskellDebugProcess)
: RealTimeCommand<ParseResult?>(SequenceOfForwardsCommand.StandardSequenceOfForwardsCallback(
                                frameBindingsAreSet,
                                syncObject,
                                sequenceLength,
                                currentStep,
                                debugProcess)) {
    override fun getText(): String = ":forward\n"

    override fun parseGHCiOutput(output: Deque<String?>): ParseResult? = null

    class object {
        private class StandardSequenceOfForwardsCallback(val frameBindingsAreSet: Condition,
                                                         val syncObject: Lock,
                                                         val sequenceLength: Int,
                                                         var currentStep: Int = 1,
                                                         val debugProcess: HaskellDebugProcess)
        : CommandCallback<ParseResult?>() {
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