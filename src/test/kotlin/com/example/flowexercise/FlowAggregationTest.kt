package com.example.flowexercise

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FlowAggregationTest {

    private val prefs = UserPrefs(theme = "dark", language = "en")

    @Test
    fun `emits loading when any source is loading`() = runTest {
        val designFlow = flowOf<Result<Design>>(Result.Loading)
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Success(emptyList()))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            assertIs<Result.Loading>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `emits success with all data when all sources succeed`() = runTest {
        val design = Design(id = "1", name = "Test Design")
        val templates = listOf(Template(id = "t1", title = "Template 1"))

        val designFlow = flowOf<Result<Design>>(Result.Success(design))
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Success(templates))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            val result = awaitItem()
            assertIs<Result.Success<DesignScreenState>>(result)
            assertEquals(design, result.data.design)
            assertEquals(templates, result.data.templates)
            assertEquals(prefs, result.data.userPrefs)
            awaitComplete()
        }
    }

    @Test
    fun `emits error with exact message when one source fails`() = runTest {
        val designFlow = flowOf<Result<Design>>(Result.Error(Exception("Design load failed")))
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Success(emptyList()))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            val result = awaitItem()
            assertIs<Result.Error>(result)
            // A single failure must NOT add a separator.
            assertEquals("Design load failed", result.exception.message)
            awaitComplete()
        }
    }

    @Test
    fun `aggregates multiple errors in source order joined by pipe`() = runTest {
        // design + templates fail, userPrefs succeeds.
        val designFlow = flowOf<Result<Design>>(Result.Error(Exception("Design failed")))
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Error(Exception("Templates failed")))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            val result = awaitItem()
            assertIs<Result.Error>(result)
            // Both messages, in source order, joined by " | " — exact.
            assertEquals("Design failed | Templates failed", result.exception.message)
            awaitComplete()
        }
    }

    @Test
    fun `loading takes priority over error`() = runTest {
        val designFlow = flowOf<Result<Design>>(Result.Loading)
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Error(Exception("Templates failed")))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            assertIs<Result.Loading>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `error takes priority over success`() = runTest {
        // Two sources succeed, one fails -> must be Error, never Success.
        val design = Design(id = "1", name = "Test Design")
        val designFlow = flowOf<Result<Design>>(Result.Success(design))
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Error(Exception("Templates failed")))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            val result = awaitItem()
            assertIs<Result.Error>(result)
            assertEquals("Templates failed", result.exception.message)
            awaitComplete()
        }
    }

    @Test
    fun `emits reactively when any source updates`() = runTest {
        val design1 = Design(id = "1", name = "Design 1")
        val design2 = Design(id = "1", name = "Design 2")

        val designFlow = flow<Result<Design>> {
            emit(Result.Success(design1))
            delay(10)
            emit(Result.Success(design2))
        }
        val templatesFlow = flowOf<Result<List<Template>>>(Result.Success(emptyList()))
        val userPrefsFlow = flowOf<Result<UserPrefs>>(Result.Success(prefs))

        aggregateDesignData(designFlow, templatesFlow, userPrefsFlow).test {
            val r1 = awaitItem()
            assertIs<Result.Success<DesignScreenState>>(r1)
            assertEquals(design1, r1.data.design)

            val r2 = awaitItem()
            assertIs<Result.Success<DesignScreenState>>(r2)
            assertEquals(design2, r2.data.design)

            awaitComplete()
        }
    }
}
