package com.harlie.dogs

import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.harlie.dogs.util.GlideWrapper
import com.harlie.dogs.view.MainActivity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class ExampleInstrumentedTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @MockK
    lateinit var mockList: List<String>

    companion object {
        @BeforeClass
        @JvmStatic
        fun preInitialization() {
            System.out.println("preInitialization")
            GlideWrapper.isUnitTest = true
        }

        @AfterClass
        @JvmStatic
        fun allTestsComplete() {
            System.out.println("allTestsComplete")
        }
    }

    @Before
    fun setup() {
        System.out.println("setup")
        MockKAnnotations.init(this)
    }

    @Test
    fun dummyInstrumentedTest() {
        System.out.println("dummyInstrumentedTest")
        every {mockList.size} returns 5
        assertTrue(mockList.size == 5, "Expect the List size is 5")
    }

    @Test
    fun testThatContextIsForDogsPackage() {
        // test Context of the app under test.
        System.out.println("testThatContextIsForDogsPackage")
        val context = MyApplication.applicationContext()
        assertEquals("com.harlie.dogs", context.packageName)
    }

    @After
    fun teardown() {
        System.out.println("teardown")
    }
}
