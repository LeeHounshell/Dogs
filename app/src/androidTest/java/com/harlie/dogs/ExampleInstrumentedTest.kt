package com.harlie.dogs

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class ExampleInstrumentedTest {

    @MockK
    lateinit var mockList: List<String>

    @Before
    fun setup() {
        System.out.println("setup")
        MockKAnnotations.init(this)
    }

    @Test
    fun useAppContext() {
        // test Context of the app under test.
        System.out.println("useAppContext")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.harlie.dogs", appContext.packageName)
    }

    @Test
    fun dummyInstrumentedTest() {
        System.out.println("dummyInstrumentedTest")
        every {mockList.size} returns 5
        assertTrue(mockList.size == 5, "Expect the List size is 5")
    }

    @After
    fun teardown() {
        System.out.println("teardown")
    }
}
