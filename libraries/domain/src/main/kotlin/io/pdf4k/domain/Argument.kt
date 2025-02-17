package io.pdf4k.domain

import io.pdf4k.domain.Argument.*
import java.awt.Color

sealed class Argument {
    abstract val name: String

    data class BooleanArgument(override val name: String, val value: Boolean): Argument()
    data class StringArgument(override val name: String, val value: String): Argument()
    data class IntArgument(override val name: String, val value: Int): Argument()
    data class FloatArgument(override val name: String, val value: Float): Argument()
    data class ObjectArgument(override val name: String, val value: List<Argument>): Argument()
    data class ResourceArgument(override val name: String, val value: ResourceLocation): Argument()
    data class ColourArgument(override val name: String, val value: Color): Argument()
}

fun Boolean?.toArgument(name: String) = this?.let { BooleanArgument(name, this) }
fun Color?.toArgument(name: String) = this?.let { ColourArgument(name, this) }
fun String?.toArgument(name: String) = this?.let { StringArgument(name, this) }
fun Int?.toArgument(name: String) = this?.let { IntArgument(name, this) }
fun Float?.toArgument(name: String) = this?.let { FloatArgument(name, this) }
fun <T : Any> T?.toArgument(name: String, block: (T) -> List<Argument?>) = this?.let {
    ObjectArgument(name, block(this).mapNotNull { it })
}
fun ResourceLocation?.toArgument(name: String) = this?.let { ResourceArgument(name, this)}

fun List<Argument>.named(name: String) = firstOrNull { it.name == name }

private fun <T> argumentError(): () -> T = { throw PdfError.RenderingError(null) }

fun Argument?.nullableBoolean() = (this as? BooleanArgument)?.value
fun Argument?.booleanValue(orElse: () -> Boolean = argumentError()) = when (this) {
    null -> orElse()
    is BooleanArgument -> value
    else -> orElse()
}

fun Argument?.nullableString() = (this as? StringArgument)?.value
fun Argument?.stringValue(orElse: () -> String = argumentError()) = when (this) {
    null -> orElse()
    is StringArgument -> value
    else -> orElse()
}

fun Argument?.nullableFloat() = (this as? FloatArgument)?.value
fun Argument?.floatValue(orElse: () -> Float = argumentError()) = when (this) {
    null -> orElse()
    is FloatArgument -> value
    else -> orElse()
}

fun Argument?.nullableInt() = (this as? IntArgument)?.value
fun Argument?.intValue(orElse: () -> Int = argumentError()) = when (this) {
    null -> orElse()
    is IntArgument -> value
    else -> orElse()
}

fun Argument?.nullableColour() = (this as? ColourArgument)?.value
fun Argument?.colourValue(orElse: () -> Color = argumentError()) = when (this) {
    null -> orElse()
    is ColourArgument -> value
    else -> orElse()
}

fun Argument?.nullableResource() = (this as? ResourceArgument)?.value
fun Argument?.resourceValue(orElse: () -> ResourceLocation = argumentError()) = when (this) {
    null -> orElse()
    is ResourceArgument -> value
    else -> orElse()
}

fun <T> Argument?.nullableObject(block: (List<Argument>) -> T) = (this as? ObjectArgument)?.value?.let(block)
fun <T> Argument?.objectValue(orElse: () -> T = argumentError(), block: (List<Argument>) -> T) = when (this) {
    null -> orElse()
    is ObjectArgument -> block(value)
    else -> orElse()
}