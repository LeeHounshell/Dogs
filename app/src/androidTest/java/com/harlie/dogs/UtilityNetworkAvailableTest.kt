package com.harlie.dogs

import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.harlie.dogs.util.GlideWrapper
import com.harlie.dogs.util.isNetworkAvailable
import com.harlie.dogs.view.MainActivity
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class UtilityNetworkAvailableTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    companion object {
        @BeforeClass
        @JvmStatic
        fun preInitialization() {
            println("preInitialization")
            GlideWrapper.isUnitTest = true
        }

        @AfterClass
        @JvmStatic
        fun allTestsComplete() {
            println("allTestsComplete")
        }
    }

    @Before
    fun setup() {
        println("setup")
    }

    @Test
    fun enableNetwork_then_TestNetwork_is_Enabled() {
        println("enableNetwork_then_TestNetwork_is_Enabled")
        GlideWrapper.isUnitTest = false
        mockkStatic("com.harlie.dogs.util.UtilityFunctionsKt") to {
            every { isNetworkAvailable() } returns true
        }
        assert(isNetworkAvailable())
    }

    @Test
    fun disableNetwork_then_TestNetwork_is_Disabled() {
        println("disableNetwork_then_TestNetwork_is_Disabled")
        GlideWrapper.isUnitTest = false
        mockkStatic("com.harlie.dogs.util.UtilityFunctionsKt") to {
            every { isNetworkAvailable() } returns false
        }
        assert(!isNetworkAvailable())
    }

    @After
    fun teardown() {
        println("teardown")
        GlideWrapper.isUnitTest = true
    }
}
