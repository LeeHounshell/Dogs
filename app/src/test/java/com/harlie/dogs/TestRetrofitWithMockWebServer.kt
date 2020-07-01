package com.harlie.dogs

import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogsApiService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.HttpURLConnection

// use MockWebServer to verify network responses are converted into correct data structures
@Config(sdk = [27, 28])
@RunWith(RobolectricTestRunner::class)
class TestRetrofitWithMockWebServer {

    private var mockWebServer = MockWebServer()
    private val dogsApiService = DogsApiService()
    private val dogDataJson = "dog_data.json"
    private var dogsCount = 0

    private lateinit var content: String

    @Before
    fun setup() {
        println("setup")
        content = FileUtil().loadResource(dogDataJson)
        //println("setup: content=${content}")
        dogsApiService.setRealBaseUrl(mockWebServer.url("/").toString())
    }

    @Test
    fun testRetrofitAPI() {
        println("testRetrofitAPI")

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(content)

        mockWebServer.enqueue(response)

        println("response=${response.status}")
        assert(response.status == "HTTP/1.1 200 OK")
        println("response body=${response.getBody()}")

        val dogsListFlowable = dogsApiService.getRequestApi().getFlowableDogs()
        dogsListFlowable
            .forEach {
                validateItems(it)
            }
            .dispose()
    }

    private fun validateItems(it: List<DogBreed>?) {
        println("validateItems")
        it?.forEach {
            when (++dogsCount) {
                1 -> {
                    assert(it.breedId == "1")
                    assert(it.breedName == "Affenpinscher")
                    assert(it.breedLifespan == "10 - 12 years")
                    assert(it.breedGroup == "Toy")
                    assert(it.breedPurpose == "Small rodent hunting, lapdog")
                    assert(it.breedTemperament == "Stubborn, Curious, Playful, Adventurous, Active, Fun-loving")
                    assert(it.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/1.jpg")
                }
                2 -> {
                    assert(it.breedId == "2")
                    assert(it.breedName == "Afghan Hound")
                    assert(it.breedLifespan == "10 - 13 years")
                    assert(it.breedGroup == "Hound")
                    assert(it.breedPurpose == "Coursing and hunting")
                    assert(it.breedTemperament == "Aloof, Clownish, Dignified, Independent, Happy")
                    assert(it.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/2.jpg")
                }
                3 -> {
                    assert(it.breedId == "3")
                    assert(it.breedName == "African Hunting Dog")
                    assert(it.breedLifespan == "11 years")
                    assert(it.breedGroup == null)
                    assert(it.breedPurpose == "A wild pack animal")
                    assert(it.breedTemperament == "Wild, Hardworking, Dutiful")
                    assert(it.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/3.jpg")
                }
                4 -> {
                    assert(it.breedId == "4")
                    assert(it.breedName == "Airedale Terrier")
                    assert(it.breedLifespan == "10 - 13 years")
                    assert(it.breedGroup == "Terrier")
                    assert(it.breedPurpose == "Badger, otter hunting")
                    assert(it.breedTemperament == "Outgoing, Friendly, Alert, Confident, Intelligent, Courageous")
                    assert(it.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/4.jpg")
                }
                5 -> {
                    assert(it.breedId == "5")
                    assert(it.breedName == "Akbash Dog")
                    assert(it.breedLifespan == "10 - 12 years")
                    assert(it.breedGroup == "Working")
                    assert(it.breedPurpose == "Sheep guarding")
                    assert(it.breedTemperament == "Loyal, Independent, Intelligent, Brave")
                    assert(it.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/5.jpg")
                }
                else -> {
                    // ignore the remaining JSON data
                }
            }
        }
        // ensure there are 172 DogBreed objects in the test JSON
        assert(dogsCount == 172)
    }

    @After
    fun teardown() {
        println("teardown")
        mockWebServer.shutdown()
    }

}
