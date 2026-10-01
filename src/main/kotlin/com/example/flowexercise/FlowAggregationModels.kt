package com.example.flowexercise

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class Design(
    val id: String,
    val name: String,
)

data class Template(
    val id: String,
    val title: String,
)

data class UserPrefs(
    val theme: String,
    val language: String,
)

/**
 * Aggregated screen data — DATA ONLY.
 *
 * Loading and error are represented by the [Result] wrapper around this state,
 * so they are intentionally NOT duplicated here. The state exists only when every
 * source has succeeded; until then there is nothing to hold. Keep it that way
 * (single source of truth for lifecycle).
 */
data class DesignScreenState(
    val design: Design,
    val templates: List<Template>,
    val userPrefs: UserPrefs,
)

/**
 * A typical UI result wrapper. [Loading] and [Error] carry no element type, so they are
 * shared and typed as Result<Nothing> via the `out` variance.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Exception) : Result<Nothing>
    data object Loading : Result<Nothing>
}

/**
 * EXERCISE: implement aggregateDesignData()
 *
 * Combine three independent Result streams into ONE Result<DesignScreenState> stream.
 *
 * Requirements — the PRIORITY ORDER matters:
 *   1. LOADING  — if ANY source is Loading -> emit Result.Loading.
 *   2. ERROR    — else if ANY source is Error -> emit a single Result.Error whose
 *                 message is ALL failing messages joined by " | " in SOURCE ORDER
 *                 (design, then templates, then userPrefs).
 *                 A single failure => just that one message, no separator.
 *   3. SUCCESS  — else (all three Success) -> emit Result.Success(DesignScreenState(...)).
 *   4. Priority is strictly  Loading > Error > Success.
 *   5. Re-emit reactively whenever ANY source emits a new value.
 *
 * Run: ./gradlew test      (all 7 tests must pass)
 */
fun aggregateDesignData(
    designFlow: Flow<Result<Design>>,
    templatesFlow: Flow<Result<List<Template>>>,
    userPrefsFlow: Flow<Result<UserPrefs>>,
): Flow<Result<DesignScreenState>> {
    TODO("Implement aggregateDesignData()")
}
