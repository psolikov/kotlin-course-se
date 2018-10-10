package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.lang.RuntimeException
import java.util.*

class DSLTest {

    private lateinit var baos: ByteArrayOutputStream

    @Before
    fun setup() {
        baos = ByteArrayOutputStream()
    }

    @Test
    fun testFirst() {
        val rows = Arrays.asList(1, 2)
        document {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in rows) {
                        item { +"$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag("pyglist", "language" to "kotlin") {
                    +"""
               |val a = 1
               |
            """.trimMargin()
                }
            }
        }.toOutputStream(baos)
        assertEquals("\\documentclass{beamer}\n" +
                "\\usepackage[russian]{babel}\n" +
                "\\begin{document}\n" +
                "\\begin{frame}{frametitle}[arg1=arg2]\n" +
                "\\begin{itemize}\n" +
                "\\begin{item}\n" +
                "1 text\n" +
                "\\end{item}\n" +
                "\\begin{item}\n" +
                "2 text\n" +
                "\\end{item}\n" +
                "\\end{itemize}\n" +
                "\\begin{pyglist}[language=kotlin]\n" +
                "val a = 1\n" +
                "\n" +
                "\\end{pyglist}\n" +
                "\\end{frame}\n" +
                "\\end{document}\n", baos.toString())
    }

    @Test
    fun testDocument() {
        document {
            +"Nothing here"
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "Nothing here\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testDocumentClass() {
        document {
            documentClass("TestClass")
            +"Nothing here"
        }.toOutputStream(baos)
        assertEquals(
                "\\documentclass{TestClass}\n" +
                        "\\begin{document}\n" +
                        "Nothing here\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testUsePackage() {
        document {
            usepackage("TestUP")
            +"Nothing here"
        }.toOutputStream(baos)
        assertEquals(
                "\\usepackage{TestUP}\n" +
                        "\\begin{document}\n" +
                        "Nothing here\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testItemize() {
        document {
            itemize {
                item { +"One" }
                item { +"Two" }
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{itemize}\n" +
                        "\\begin{item}\n" +
                        "One\n" +
                        "\\end{item}\n" +
                        "\\begin{item}\n" +
                        "Two\n" +
                        "\\end{item}\n" +
                        "\\end{itemize}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testEnumerate() {
        document {
            enumerate {
                enum { +"One" }
                enum { +"Two" }
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{enumerate}\n" +
                        "\\begin{enum}\n" +
                        "One\n" +
                        "\\end{enum}\n" +
                        "\\begin{enum}\n" +
                        "Two\n" +
                        "\\end{enum}\n" +
                        "\\end{enumerate}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testEnumerateInsideItemize() {
        document {
            itemize {
                item {
                    enumerate {
                        enum { +"Kek" }
                    }
                }
                item { +"Two" }
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{itemize}\n" +
                        "\\begin{item}\n" +
                        "\\begin{enumerate}\n" +
                        "\\begin{enum}\n" +
                        "Kek\n" +
                        "\\end{enum}\n" +
                        "\\end{enumerate}\n" +
                        "\\end{item}\n" +
                        "\\begin{item}\n" +
                        "Two\n" +
                        "\\end{item}\n" +
                        "\\end{itemize}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testFrame() {
        document {
            frame("The Doors", "Five" to "one") {
                +"Baby, one in five"
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{frame}{The Doors}[Five=one]\n" +
                        "Baby, one in five\n" +
                        "\\end{frame}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testMath() {
        document {
            math {
                +"This is math"
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{math}\n" +
                        "This is math\n" +
                        "\\end{math}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testAlign() {
        document {
            align {
                +"This is align"
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{align*}\n" +
                        "This is align\n" +
                        "\\end{align*}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test
    fun testCustomTag() {
        document {
            customTag("name", "string" to "string") {
                +"This is custom tag"
            }
        }.toOutputStream(baos)
        assertEquals(
                "\\begin{document}\n" +
                        "\\begin{name}[string=string]\n" +
                        "This is custom tag\n" +
                        "\\end{name}\n" +
                        "\\end{document}\n", baos.toString())
    }

    @Test(expected = RuntimeException::class)
    fun testTwoDocumentClass() {
        document {
            documentClass("One")
            documentClass("Two")
        }.toOutputStream(baos)
    }
}