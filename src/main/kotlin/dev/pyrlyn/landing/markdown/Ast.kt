package dev.pyrlyn.landing.markdown

/** Block-level Markdown nodes (the GFM subset the synced docs use). */
sealed interface Block {
    data class Heading(val depth: Int, val text: String) : Block
    data class Paragraph(val text: String) : Block
    data class Code(val text: String, val lang: String) : Block
    data class ListBlock(val ordered: Boolean, val start: Int, val items: List<Item>, val loose: Boolean) : Block
    data class Item(val blocks: List<Block>, val task: Boolean? = null)
    data class Quote(val blocks: List<Block>) : Block
    data class Table(val header: List<String>, val aligns: List<String?>, val rows: List<List<String>>) : Block
    data class Html(val raw: String) : Block
    data object Rule : Block
}

/** Inline nodes. */
sealed interface Inline {
    data class Text(val text: String) : Inline
    data class CodeSpan(val text: String) : Inline
    data class Em(val children: List<Inline>) : Inline
    data class Strong(val children: List<Inline>) : Inline
    data class Del(val children: List<Inline>) : Inline
    data class Link(val href: String, val title: String?, val children: List<Inline>) : Inline
    data class Image(val href: String, val alt: String) : Inline
    data class Html(val raw: String) : Inline
    data object Break : Inline
}
