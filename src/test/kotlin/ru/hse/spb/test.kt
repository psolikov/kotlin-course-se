package ru.hse.spb

import org.junit.Test
import java.io.ByteArrayOutputStream
import org.junit.After
import java.io.PrintStream
import org.junit.Before


class Tests {
    private val outContent = ByteArrayOutputStream()
    private val originalOut = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
    }

    @After
    fun restoreStreams() {
        System.setOut(originalOut)
    }

    @Test
    fun testSimple1() {
        val graph = Graph(4)
        graph.addEdge(1, 3)
        graph.addEdge(4, 3)
        graph.addEdge(4, 2)
        graph.addEdge(1, 2)
        graph.findCycle()
        graph.findDistances()
        graph.printDistances()
        assert(outContent.toString() == "0 0 0 0 ")
    }

    @Test
    fun testSimple2() {
        val graph = Graph(6)
        graph.addEdge(1, 2)
        graph.addEdge(3, 4)
        graph.addEdge(6, 4)
        graph.addEdge(2, 3)
        graph.addEdge(1, 3)
        graph.addEdge(3, 5)
        graph.findCycle()
        graph.findDistances()
        graph.printDistances()
        assert(outContent.toString() == "0 0 0 1 1 2 ")
    }
}