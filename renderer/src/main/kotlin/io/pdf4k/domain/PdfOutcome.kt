package io.pdf4k.domain

import io.pdf4k.domain.Outcome.Failure
import io.pdf4k.domain.Outcome.Success
import io.pdf4k.renderer.PdfError

typealias PdfOutcome<T> = Outcome<T, PdfError>

fun <T> success(result: T) = Success<T, PdfError>(result)
fun <T> failure(error: PdfError) = Failure<T, PdfError>(error)
