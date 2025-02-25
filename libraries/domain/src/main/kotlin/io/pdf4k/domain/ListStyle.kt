package io.pdf4k.domain

sealed interface ListStyle {
    val startAt: Int?

    fun getListSymbol(nextListItemNumber: Int): String

    data class Symbol(val symbol: String? = null) : ListStyle {
        override fun getListSymbol(nextListItemNumber: Int): String {
            return symbol ?: "- "
        }

        override val startAt = null
    }

    data class Numbered(override val startAt: Int? = null) : ListStyle {
        override fun getListSymbol(nextListItemNumber: Int): String {
            return "$nextListItemNumber. "
        }
    }
}