package org.jetbrains.haskell.parser.lexer

import java.util.HashMap
import java.util.ArrayList
import java.util.LinkedList
import java.util.Collections
import java.util.HashSet
import java.util.TreeSet

/**
 * Created by atsky on 3/10/14.
 */

fun <A>makeState(s1: State<A>?, s2: State<A>?): State<A> {
    if (s1?.data == null) {
        return State(s2?.data)
    } else {
        return State(s1?.data)
    }
}

fun <A>merge(f1: State<A>, f2: State<A>): State<A> {
    val map: MutableMap<Pair<State<A>?, State<A>?>, State<A>> = HashMap()
    val start = Pair(f1, f2);
    val resultState = makeState(f1, f2);
    map[start] = resultState;

    val toUpdate = LinkedList<Pair<State<A>?, State<A>?>>()
    toUpdate.add(start)

    while (!toUpdate.empty) {
        val currentPair = toUpdate.removeFirst()
        val currentState = map[currentPair]!!

        val chars = HashSet<Char>()
        chars.addAll(currentPair.first?.edges?.keySet() ?: Collections.emptySet())
        chars.addAll(currentPair.second?.edges?.keySet() ?: Collections.emptySet())

        for (char in chars) {
            val s1 = currentPair.first?.next(char)
            val s2 = currentPair.second?.next(char)
            if (s1 != null || s2 != null) {
                val newPair = Pair(s1, s2)
                if (!map.containsKey(newPair)) {
                    map[newPair] = makeState(s1, s2)
                    toUpdate.add(newPair)
                }
                currentState.edges[char] = map[newPair]!!
            } else {
                currentState.edges[char] = null
            }
        }

        val s1 = currentPair.first?.other
        val s2 = currentPair.second?.other
        if (s1 != null || s2 != null) {
            val newPair = Pair(s1, s2)
            if (!map.containsKey(newPair)) {
                map[newPair] = makeState(s1, s2)
                toUpdate.add(newPair)
            }
            currentState.other = map[newPair]!!
        }
    }

    return resultState;
}


fun str(str: String): State<Boolean> {
    var current = State(true);
    for (ch in str.reverse()) {
        val prev = State<Boolean>(null)
        prev.edges[ch] = current
        current = prev
    }
    return current;
}


fun oneOf(str: Iterable<Char>): State<Boolean> {
    val fsm = State<Boolean>(null);

    val end = State<Boolean>(true);
    fsm.addAll(str, end)

    return fsm;
}

fun oneOf(str: String): State<Boolean> {
    val fsm = State<Boolean>(null);

    val end = State<Boolean>(true);
    fsm.addAll(str.toCharList(), end)

    return fsm;
}

fun anything(): State<Boolean> {
    val fsm = State<Boolean>(true);
    fsm.other = fsm
    return fsm
}

fun noneOfStar(str: String): State<Boolean> {
    val fsm = State<Boolean>(true);

    for (ch in str) {
        fsm.edges.put(ch, null)
    }
    fsm.other = fsm

    return fsm;
}

fun noneOf(str: String): State<Boolean> {
    val fsm = State<Boolean>(null);
    val end = State<Boolean>(true);

    for (ch in str) {
        fsm.edges.put(ch, null)
    }
    fsm.other = end

    return fsm;
}

fun State<Boolean>.plus(): State<Boolean> = this + this.star()


fun State<Boolean>.star(): State<Boolean> {
    val start = copy()

    start.trace {
        if (it.data != null) {
            it.epsilon.add(start)
        }
    }
    start.data = true

    return removeEpsilon(start)
}

fun makeState(s1: List<State<Boolean>>): State<Boolean> {
    return State(
            if (s1.any { it.data == true }) {
                true
            } else {
                null
            })
}

fun removeEpsilon(f: State<Boolean>): State<Boolean> {
    val map: MutableMap<List<State<Boolean>>, State<Boolean>> = HashMap()
    val start = listOf(f);
    val resultState = makeState(start);
    map[start] = resultState;

    val toUpdate = LinkedList<List<State<Boolean>>>()
    toUpdate.add(start)

    while (!toUpdate.empty) {
        val currentMultistate = toUpdate.removeFirst()
        val currentState = map[currentMultistate]!!

        val chars = HashSet<Char>()
        for (s in currentMultistate) {
            chars.addAll(s.edges.keySet())
        }

        for (char in chars) {
            val nextStates = TreeSet<State<Boolean>>()
            for (s in currentMultistate) {
                val next = s.next(char)
                if (next != null) {
                    nextStates.add(next)
                    nextStates.addAll(next.epsilon)
                }
            }
            if (!nextStates.empty) {
                val newMultystate = ArrayList(nextStates)
                if (!map.containsKey(newMultystate)) {
                    map[newMultystate] = makeState(newMultystate)
                    toUpdate.add(newMultystate)
                }
                currentState.edges[char] = map[newMultystate]!!
            } else {
                currentState.edges[char] = null
            }
        }




        val nextOtherStates = TreeSet<State<Boolean>>()
        for (s in currentMultistate) {
            val next = s.other
            if (next != null) {
                nextOtherStates.add(next)
                nextOtherStates.addAll(next.epsilon)
            }
        }
        if (!nextOtherStates.empty) {
            val newMultystate = ArrayList(nextOtherStates)
            if (!map.containsKey(newMultystate)) {
                map[newMultystate] = makeState(newMultystate)
                toUpdate.add(newMultystate)
            }
            currentState.other = map[newMultystate]!!
        }

    }

    return resultState;
}


public fun State<Boolean>.plus(another: State<Boolean>): State<Boolean> {
    val start = this.copy()
    start.trace {
        if (it.data != null) {
            it.data = null
            it.epsilon.add(another)
        }
    }
    return removeEpsilon(start)
}

public fun not(fsm: State<Boolean>): State<Boolean> {
    val drain = State<Boolean>(true)
    drain.other = drain

    val newFsm = fsm.copy()
    newFsm.trace {
        if (it.data == null) {
            it.data = true
        } else {
            it.data = null
        }
        for ((key, value) in HashMap(it.edges)) {
            if (value == null) {
                it.edges[key] = drain;
            }
        }
        if (it.other == null) {
            it.other = drain
        }
    }
    return newFsm;
}

class FsmBuilder<S>() {
    var fsm = State<S>(null);

    fun add(e : S, f2 : State<Boolean>) {
        fsm = merge(fsm, f2.replace(e))
    }

    fun add(f2 : State<S>) {
        fsm = merge(fsm, f2)
    }
}

fun <S>buildFsm(init : FsmBuilder<S>.()->Unit) : State<S> {
    val fsmBuilder = FsmBuilder<S>();
    fsmBuilder.init()
    return fsmBuilder.fsm;
}


class State<A : Any>(public var data: A?) : Comparable<State<A>> {
    public val epsilon: MutableList<State<A>> = ArrayList<State<A>>()
    public val edges: MutableMap<Char, State<A>?> = HashMap<Char, State<A>?>()
    public var other: State<A>? = null

    fun addAll(chars: CharRange, next: State<A>) {
        for (ch in chars) {
            edges.put(ch, next)
        }
    }

    fun addAll(chars: Iterable<Char>, next: State<A>) {
        for (ch in chars) {
            edges.put(ch, next)
        }
    }

    fun next(char: Char): State<A>? =
        if (edges.containsKey(char)) {
            edges[char]
        } else {
            other
        }


    fun copy() = copy(HashMap())

    fun copy(map: MutableMap<State<A>, State<A>>): State<A> {
        if (!map.containsKey(this)) {
            map[this] = State<A>(data)

            val result = map[this]!!

            for ((key, value) in edges) {
                result.edges[key] = value?.copy(map)
            }

            result.other = other?.copy(map)

            return result
        } else {
            return map[this]!!
        }
    }


    fun trace(f: (State<A>) -> Unit) {
        trace(f, HashSet())
    }

    fun trace(f: (State<A>) -> Unit, done : HashSet<State<A>>) {
        if (done.contains(this)) {
            return
        }
        done.add(this)
        for ((key, value) in edges) {
            value?.trace(f, done)
        }
        this.other?.trace(f, done)
        f(this)
    }

    fun <B>replace(data: B): State<B> {
        return replace(HashMap(), data)
    }

    fun <B>replace(map: MutableMap<State<A>, State<B>>, newData: B): State<B> {
        if (map.containsKey(this)) {
            return map[this]!!
        }

        if (data == null) {
            map[this] = State<B>(null)
        } else {
            map[this] = State<B>(newData)
        }

        val result = map[this]!!

        for ((key, value) in edges) {
            result.edges[key] = value?.replace(map, newData)
        }

        result.other = other?.replace(map, newData)

        return result
    }


    override fun compareTo(other : State<A>) : Int {
        return hashCode().compareTo(other.hashCode())
    }


}

