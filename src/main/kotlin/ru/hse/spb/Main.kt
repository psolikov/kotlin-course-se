package ru.hse.spb

import java.io.OutputStream
import java.io.PrintWriter
import java.util.*

@DslMarker
annotation class TexTagMarker

interface Element {
    fun render(builder: StringBuilder)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n")
    }
}

@TexTagMarker
abstract class Tag(private val name: String, val root: Document?) : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, Pair<Int, String>>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder) {
        builder.append("\\begin{$name}${renderAttributes()}\n")
        for (c in children) {
            c.render(builder)
        }
        builder.append("\\end{$name}\n")
    }

    private fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((_, value) in attributes.values.filter { it.first == 1 }) {
            builder.append("{$value}")
        }
        if (attributes.values.any { it.first == 2 }) {
            builder.append("[")
            var i = 0
            val size = attributes.values.filter { it.first == 2 }.size
            for ((_, value) in attributes.values.filter { it.first == 2 }) {
                if (i == size - 1) {
                    builder.append(value)
                } else {
                    builder.append(",$value")
                }
                i++
            }
            builder.append("]")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder)
        return builder.toString()
    }
}

abstract class TagWithText(name: String, root: Document?) : Tag(name, root) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

class Document : TagWithText("document", null) {
    var documentClassInstance: String? = null
    val header = StringBuilder()

    fun frame(frameTitle: String, pair: Pair<String, String>, init: Frame.() -> Unit) {
        val frame = initTag(Frame(root), init)
        frame.frameTitle = Pair(1, frameTitle)
        frame.pair = Pair(2, "${pair.first}=${pair.second}")
    }

    fun customTag(name: String, pair: Pair<String, String>, init: CustomTag.() -> Unit) {
        val cTag = initTag(CustomTag(name, root), init)
        cTag.pair = Pair(2, "${pair.first}=${pair.second}")
    }

    fun documentClass(arg: String) = DocumentClass(arg, this)

    fun usepackage(arg: String, vararg args: String) = Usepackage(this, arg, *args)

    fun itemize(init: Itemize.() -> Unit) {
        initTag(Itemize(root), init)
    }

    fun enumerate(init: Enumerate.() -> Unit) {
        initTag(Enumerate(root), init)
    }

    fun math(init: ru.hse.spb.Math.() -> Unit) {
        initTag(ru.hse.spb.Math(root), init)
    }

    fun align(init: ru.hse.spb.Align.() -> Unit) {
        initTag(ru.hse.spb.Align(root), init)
    }

    fun toOutputStream(os: OutputStream) {
        val pw = PrintWriter(os)
        if (documentClassInstance != null) pw.write(documentClassInstance)
        pw.write(header.toString())
        pw.write(toString())
        pw.flush()
    }
}

class Frame(root: Document?) : BlockWithBody("frame", root) {
    var frameTitle: Pair<Int, String>
        get() = attributes["frameTitle"]!!
        set(value) {
            attributes["frameTitle"] = value
        }
    var pair: Pair<Int, String>
        get() = attributes["pair"]!!
        set(value) {
            attributes["pair"] = value
        }
}

class CustomTag(name: String, root: Document?) : BlockWithBody(name, root) {
    var pair: Pair<Int, String>
        get() = attributes["pair"]!!
        set(value) {
            attributes["pair"] = value
        }
}

class DocumentClass(arg: String, root: Document?) : TagWithText("documentclass", root) {
    init {
        if (root?.documentClassInstance == null) root?.documentClassInstance = "\\documentclass{$arg}\n"
        else throw RuntimeException("Duplicated documentclass")
    }
}

class Usepackage(root: Document?, arg: String, vararg args: String) : TagWithText("usepackage", root) {
    init {
        if (root?.header != null) {
            root.header.append("\\usepackage")
            if (args.isNotEmpty()) {
                root.header.append("[")
                for (i in 0 until args.size - 1) {
                    root.header.append("${args[i]}, ")
                }
                root.header.append(args[args.size - 1])
                root.header.append("]")
            }
            root.header.append("{$arg}\n")
        } else throw RuntimeException("No usepackage provided")
    }
}

abstract class BlockWithBody(name: String, root: Document?) : TagWithText(name, root) {
    fun customTag(name: String, pair: Pair<String, String>, init: CustomTag.() -> Unit) {
        val cTag = initTag(CustomTag(name, root), init)
        cTag.pair = Pair(2, "${pair.first}=${pair.second}")
    }

    fun itemize(init: Itemize.() -> Unit) {
        initTag(Itemize(root), init)
    }

    fun enumerate(init: Enumerate.() -> Unit) {
        initTag(Enumerate(root), init)
    }

    fun math(init: ru.hse.spb.Math.() -> Unit) {
        initTag(ru.hse.spb.Math(root), init)
    }

    fun align(init: ru.hse.spb.Align.() -> Unit) {
        initTag(ru.hse.spb.Align(root), init)
    }
}

class Align(root: Document?) : TagWithText("align*", root)
class Math(root: Document?) : TagWithText("math", root)

class Itemize(root: Document?) : TagWithText("itemize", root) {
    fun item(init: Item.() -> Unit) {
        initTag(Item(root), init)
    }
}

class Enumerate(root: Document?) : TagWithText("enumerate", root) {
    fun enum(init: Enum.() -> Unit) {
        initTag(Enum(root), init)
    }
}

class Item(root: Document?) : BlockWithBody("item", root)
class Enum(root: Document?) : BlockWithBody("enum", root)

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}