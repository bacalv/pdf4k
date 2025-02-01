package io.pdf4k.domain

sealed interface Outcome<T, E> {
    fun <R> map(fn: (T) -> R) = when (this) {
        is Success -> Success<R, E>(fn(result))
        is Failure -> Failure(error)
    }

    fun <R> flatMap(fn: (T) -> Outcome<R, E>): Outcome<R, E> = when (this) {
        is Success -> fn(result)
        is Failure -> Failure(error)
    }

    fun <R> combine(fn: (T) -> Outcome<R, E>): Outcome<Pair<T, R>, E> = when (this) {
        is Success -> fn(result).let {
            when (it) {
                is Success -> Success(result to it.result)
                is Failure -> Failure(it.error)
            }
        }
        is Failure -> Failure(error)
    }

    fun onSuccess(fn: (T) -> Unit) = this.also {
        when (this) {
            is Success -> fn(result)
            is Failure -> Unit
        }
    }

    fun onFailure(fn: (E) -> Unit) = this.also {
        when (this) {
            is Success -> Unit
            is Failure -> fn(error)
        }
    }

    fun getOrNull(): T? = when (this) {
        is Success -> result
        is Failure -> null
    }

    fun getOrElse(fn: (E) -> T): T = when (this) {
        is Success -> result
        is Failure -> fn(error)
    }

    data class Success<T, E>(val result: T): Outcome<T, E>
    data class Failure<T, E>(val error: E): Outcome<T, E>
}