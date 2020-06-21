package com.harlie.dogs

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.harlie.dogs.util.isNetworkAvailable
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4ClassRunner::class)
class TestMockNetworkControl {

    @RelaxedMockK
    lateinit var context: Context
    @MockK
    lateinit var connectivityManagerMock: ConnectivityManager
    @MockK
    lateinit var networkMock: Network
    @MockK
    lateinit var networkCapabilitiesMock: NetworkCapabilities
    @MockK
    lateinit var networkInfoMock: NetworkInfo

    @Before
    fun setup() {
        System.out.println("setup")
        MockKAnnotations.init(this)
    }

    @Test
    fun enableNetwork_then_TestNetwork_is_Enabled() {
        System.out.println("enableNetwork_then_TestNetwork_is_Enabled")
        enableNetworkAndVerifyNetworkIsEnabled(context)
    }

    @Test
    fun disableNetwork_then_TestNetwork_is_Disabled() {
        System.out.println("disableNetwork_then_TestNetwork_is_Disabled")
        disableNetworkAndVerifyNetworkIsDisabled(context)
    }

    // utility to run tests with the Network "enabled" (see isNetworkAvailable)
    fun enableNetworkAndVerifyNetworkIsEnabled(context: Context) {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManagerMock
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.println("enableNetworkAndVerifyNetworkIsEnabled >= Build.VERSION_CODES.M")
            every { connectivityManagerMock.activeNetwork } returns networkMock
            every { connectivityManagerMock.getNetworkCapabilities(networkMock) } returns networkCapabilitiesMock
            every { networkCapabilitiesMock.hasTransport(any()) } returns true
            assertEquals(true, isNetworkAvailable(context), "Expect the mock Network is available")
            verify { connectivityManagerMock.getNetworkCapabilities(networkMock) }
            verify { networkCapabilitiesMock.hasTransport(any()) }
        }
        else {
            System.out.println("enableNetworkAndVerifyNetworkIsEnabled < Build.VERSION_CODES.M")
            every { connectivityManagerMock.activeNetworkInfo } returns networkInfoMock
            every { networkInfoMock.isConnected } returns true
            assertEquals(true, isNetworkAvailable(context), "Expect the mock Network is available")
            verify { connectivityManagerMock.activeNetworkInfo }
            verify { networkInfoMock.isConnected }
        }
    }

    // utility to run tests with the Network "disabled" (see isNetworkAvailable)
    fun disableNetworkAndVerifyNetworkIsDisabled(context: Context) {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManagerMock
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.println("disableNetworkAndVerifyNetworkIsDisabled >= Build.VERSION_CODES.M")
            every { connectivityManagerMock.activeNetwork } returns networkMock
            every { connectivityManagerMock.getNetworkCapabilities(networkMock) } returns networkCapabilitiesMock
            every { networkCapabilitiesMock.hasTransport(any()) } returns false
            assertEquals(false, isNetworkAvailable(context), "Expect the mock Network is NOT available")
            verify { connectivityManagerMock.getNetworkCapabilities(networkMock) }
            verify { networkCapabilitiesMock.hasTransport(any()) }
        }
        else {
            System.out.println("disableNetworkAndVerifyNetworkIsDisabled < Build.VERSION_CODES.M")
            every { connectivityManagerMock.activeNetworkInfo } returns networkInfoMock
            every { networkInfoMock.isConnected } returns false
            assertEquals(false, isNetworkAvailable(context), "Expect the mock Network is NOT available")
            verify { connectivityManagerMock.activeNetworkInfo }
            verify { networkInfoMock.isConnected }
        }
    }

    @After
    fun teardown() {
        System.out.println("teardown")
    }
}