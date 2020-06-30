package com.harlie.dogs

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @MockK
    lateinit var mockList: List<String>

    @Before
    fun setup() {
        println("setup")
        MockKAnnotations.init(this)
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dummyUnitTest() {
        println("dummyUnitTest")
        every {mockList.size} returns 5
        assertTrue(mockList.size == 5, "Expect the List size is 5")
    }

    @After
    fun teardown() {
        println("teardown")
    }
}
