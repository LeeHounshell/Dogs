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
@Config(sdk = intArrayOf(27, 28))
@RunWith(RobolectricTestRunner::class)
class TestRetrofitWithMockWebServer {

    private var mockWebServer = MockWebServer()
    private val dogsApiService = DogsApiService()
    private var dogsCount = 0

    lateinit var content: String

    @Before
    fun setup() {
        System.out.println("setup")
        content = FileUtil().loadResource("dog_data.json")
        if (content.length == 0) {
            content = readFakeJsonAsset("dog_data.json")
        }
        System.out.println("setup: content=${content}")
        dogsApiService.setRealBaseUrl(mockWebServer.url("/").toString())
    }

    @Test
    fun testRetrofitAPI() {
        System.out.println("testRetrofitAPI")

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(content)

        mockWebServer.enqueue(response)

        System.out.println("response=${response.status}")
        assert(response.status == "HTTP/1.1 200 OK")
        System.out.println("response body=${response.getBody()}")

        val dogsListFlowable = dogsApiService.getRequestApi().getFlowableDogs()
        dogsListFlowable
            .forEach {
                validateItems(it)
            }
            .dispose()
    }

    private fun validateItems(it: List<DogBreed>?) {
        System.out.println("validateItems")
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
                    System.out.println("*** UNEXPECTED DATA RECEIVED: dogsCount=${dogsCount}")
                    assert(true == false)
                }
            }
        }
        assert(dogsCount == 5)
    }

    @After
    fun teardown() {
        System.out.println("teardown")
        mockWebServer.shutdown()
    }

    // FIXME: currently unable to read asset files in a unit test with no instrumentation
    private fun readFakeJsonAsset(asset_file: String): String {
        System.out.println("FIXME: simulate reading ${asset_file}")
        return "" +
        "[" +
            "{" +
                "\"bred_for\": \"Small rodent hunting, lapdog\"," +
                "\"breed_group\": \"Toy\"," +
                "\"height\": {" +
                    "\"imperial\": \"9 - 11.5\"," +
                    "\"metric\": \"23 - 29\"" +
                "}," +
                "\"id\": 1," +
                "\"life_span\": \"10 - 12 years\"," +
                "\"name\": \"Affenpinscher\"," +
                "\"origin\": \"Germany, France\"," +
                "\"temperament\": \"Stubborn, Curious, Playful, Adventurous, Active, Fun-loving\"," +
                "\"weight\": {" +
                    "\"imperial\": \"6 - 13\"," +
                    "\"metric\": \"3 - 6\"" +
                "}," +
                "\"url\": \"https://raw.githubusercontent.com/DevTides/DogsApi/master/1.jpg\"" +
            "}," +
            "{" +
                "\"bred_for\": \"Coursing and hunting\"," +
                "\"breed_group\": \"Hound\"," +
                "\"country_code\": \"AG\"," +
                "\"height\": {" +
                    "\"imperial\": \"25 - 27\"," +
                    "\"metric\": \"64 - 69\"" +
                "}," +
                "\"id\": 2," +
                "\"life_span\": \"10 - 13 years\"," +
                "\"name\": \"Afghan Hound\"," +
                "\"origin\": \"Afghanistan, Iran, Pakistan\"," +
                "\"temperament\": \"Aloof, Clownish, Dignified, Independent, Happy\"," +
                "\"weight\": {" +
                    "\"imperial\": \"50 - 60\"," +
                    "\"metric\": \"23 - 27\"" +
                "}," +
                "\"url\": \"https://raw.githubusercontent.com/DevTides/DogsApi/master/2.jpg\"" +
            "}," +
            "{" +
                "\"bred_for\": \"A wild pack animal\"," +
                "\"height\": {" +
                    "\"imperial\": \"30\"," +
                    "\"metric\": \"76\"" +
                "}," +
                "\"id\": 3," +
                "\"life_span\": \"11 years\"," +
                "\"name\": \"African Hunting Dog\"," +
                "\"origin\": \"\"," +
                "\"temperament\": \"Wild, Hardworking, Dutiful\"," +
                "\"weight\": {" +
                    "\"imperial\": \"44 - 66\"," +
                    "\"metric\": \"20 - 30\"" +
                "}," +
                "\"url\": \"https://raw.githubusercontent.com/DevTides/DogsApi/master/3.jpg\"" +
            "}," +
            "{" +
                "\"bred_for\": \"Badger, otter hunting\"," +
                "\"breed_group\": \"Terrier\"," +
                "\"height\": {" +
                    "\"imperial\": \"21 - 23\"," +
                    "\"metric\": \"53 - 58\"" +
                "}," +
                "\"id\": 4," +
                "\"life_span\": \"10 - 13 years\"," +
                "\"name\": \"Airedale Terrier\"," +
                "\"origin\": \"United Kingdom, England\"," +
                "\"temperament\": \"Outgoing, Friendly, Alert, Confident, Intelligent, Courageous\"," +
                "\"weight\": {" +
                    "\"imperial\": \"40 - 65\"," +
                    "\"metric\": \"18 - 29\"" +
                "}," +
                "\"url\": \"https://raw.githubusercontent.com/DevTides/DogsApi/master/4.jpg\"" +
            "}," +
            "{" +
                "\"bred_for\": \"Sheep guarding\"," +
                "\"breed_group\": \"Working\"," +
                "\"height\": {" +
                    "\"imperial\": \"28 - 34\"," +
                    "\"metric\": \"71 - 86\"" +
                "}," +
                "\"id\": 5," +
                "\"life_span\": \"10 - 12 years\"," +
                "\"name\": \"Akbash Dog\"," +
                "\"origin\": \"\"," +
                "\"temperament\": \"Loyal, Independent, Intelligent, Brave\"," +
                "\"weight\": {" +
                    "\"imperial\": \"90 - 120\"," +
                    "\"metric\": \"41 - 54\"" +
                "}," +
                "\"url\": \"https://raw.githubusercontent.com/DevTides/DogsApi/master/5.jpg\"" +
            "}" +
        "]"
    }

}