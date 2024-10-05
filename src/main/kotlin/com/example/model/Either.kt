package com.example.model

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of `Either` are either an instance of `Success` or `Failure`.
 *
 * @param S The type of the success value.
 * @param F The type of the failure value.
 */
sealed class Either<out S, out F> {

    /**
     * Represents a successful outcome containing a value of type [S].
     *
     * @property data The success value.
     */
    data class Success<out S>(val data: S) : Either<S, Nothing>()

    /**
     * Represents a failed outcome containing an error of type [F].
     *
     * @property error The failure value.
     */
    data class Failure<out F>(val error: F) : Either<Nothing, F>()
}
