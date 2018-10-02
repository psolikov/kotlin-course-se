package ru.hse.spb

import java.util.*
import kotlin.collections.ArrayList

class Graph(private val size: Int, private val g: List<List<Int>>) {
    private val cycle = ArrayList<Int>()
    private val distances = ArrayList<Pair<Int, Int>>()
    private val parent = ArrayList<Int>()
    private val color = ArrayList<Int>()
    private var cycleStart = -1
    private var cycleEnd = -1

    init {
        for (i in 0 until size) {
            parent.add(-1)
            color.add(0)
        }
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
        cycle.forEach(this::bfs)
        distances.sortBy(Pair<Int, Int>::first)
    }

    fun printDistances() = distances.forEach { print("${it.second} ") }

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

fun createGraph(size: Int): ArrayList<ArrayList<Int>> {
    val g = ArrayList<ArrayList<Int>>()
    for (i in 0 until size) {
        g.add(ArrayList())
    }
    return g
}

fun addEdge(g: ArrayList<ArrayList<Int>>, from: Int, to: Int) {
    g[from - 1].add(to - 1)
    g[to - 1].add(from - 1)
}

fun main(args: Array<String>) {
    val reader = Scanner(System.`in`)
    val n = reader.nextInt()
    val g = createGraph(n)
    for (i in 0 until n) {
        val from = reader.nextInt()
        val to = reader.nextInt()
        addEdge(g, from, to)
    }
    val graph = Graph(n, g)
    graph.findCycle()
    graph.findDistances()
    graph.printDistances()
}