package ru.hse.spb

import java.util.*
import kotlin.collections.ArrayList

class Graph(private val size: Int) {
    private val g: ArrayList<ArrayList<Int>> = ArrayList()
    private val cycle: ArrayList<Int> = ArrayList()
    private val distances: ArrayList<Pair<Int, Int>> = ArrayList()
    private val parent: ArrayList<Int> = ArrayList()
    private val color: ArrayList<Int> = ArrayList()
    private var cycleStart: Int = -1
    private var cycleEnd: Int = -1

    init {
        for (i in 0 until size) {
            g.add(ArrayList())
            parent.add(-1)
            color.add(0)
        }
    }

    fun addEdge(from: Int, to: Int) {
        g[from - 1].add(to - 1)
        g[to - 1].add(from - 1)
    }

    fun findCycle() {
        dfs(0)
        cycle.add(cycleStart)
        var v = cycleEnd
        while (v != cycleStart) {
            cycle.add(v)
            v = parent[v]
        }
    }

    fun findDistances() {
        distances.addAll(cycle.map { v -> Pair(v, 0) })
        cycle.forEach { v -> bfs(v) }
        distances.sortBy { pair -> pair.first }
    }

    fun printDistances() {
        distances.forEach { pair -> print(pair.second); print(" ") }
    }

    private fun dfs(v: Int): Boolean {
        color[v] = 1
        for (i in 0 until g[v].size) {
            val to: Int = g[v][i]
            if (color[to] == 0) {
                parent[to] = v
                if (dfs(to)) {
                    return true
                }
            } else if (color[to] == 1 && to != parent[v]) {
                cycleEnd = v
                cycleStart = to
                return true
            }
        }
        color[v] = 2
        return false
    }

    private fun bfs(s: Int) {
        val q = ArrayDeque<Int>()
        q.push(s)
        val used = BooleanArray(size)
        val d = IntArray(size)
        val p = IntArray(size)
        used[s] = true
        p[s] = -1
        while (!q.isEmpty()) {
            val v = q.first()
            q.pop()
            for (i in 0 until g[v].size) {
                val to = g[v][i]
                if (cycle.contains(to)) continue
                if (!used[to]) {
                    used[to] = true
                    q.push(to)
                    d[to] = d[v] + 1
                    distances.add(Pair(to, d[to]))
                    p[to] = v
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val reader = Scanner(System.`in`)
    val n: Int = reader.nextInt()
    val graph = Graph(n)
    for (i in 0 until n) {
        val from: Int = reader.nextInt()
        val to: Int = reader.nextInt()
        graph.addEdge(from, to)
    }
    graph.findCycle()
    graph.findDistances()
    graph.printDistances()
}