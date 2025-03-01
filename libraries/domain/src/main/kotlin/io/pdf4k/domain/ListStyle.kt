package io.pdf4k.domain

sealed interface ListStyle {
    val startAt: Int?

    fun getListSymbol(itemNumber: Int): String

    data class Symbol(val symbol: String? = null) : ListStyle {
        override fun getListSymbol(itemNumber: Int): String {
            return symbol ?: "- "
        }

        override val startAt = null
    }

    data class Numbered(override val startAt: Int? = null) : ListStyle {
        override fun getListSymbol(itemNumber: Int): String {
            return "$itemNumber. "
        }
    }
}