package com.harlie.dogs

import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.util.GlideWrapper
import com.harlie.dogs.util.extractArrayFromJson
import com.harlie.dogs.util.readJsonAsset
import com.harlie.dogs.view.MainActivity
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class UtilityFunctionsTest {

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
    fun test_That_readJsonAsset_Works() {
        val context = MyApplication.applicationContext()
        val rawJson: String? = readJsonAsset(context, "dog_data.json")
        assert(rawJson != null)
        assert(rawJson!!.length == 81942) // check size of the json file
    }

    @Test
    fun test_That_extractArrayFromJson_Works() {
        val context = MyApplication.applicationContext()
        val rawJson: String? = readJsonAsset(context, "dog_data.json")
        assert(rawJson != null)

        val list: MutableList<DogBreed> = extractArrayFromJson(rawJson)
        assert(list.size == 172) // the number of DogBreed in this list

        var position = 0
        val firstDog = list[position]
        assert(firstDog.breedId == "1")
        assert(firstDog.breedName == "Affenpinscher")
        assert(firstDog.breedPurpose == "Small rodent hunting, lapdog")
        assert(firstDog.breedGroup == "Toy")
        assert(firstDog.breedLifespan == "10 - 12 years")
        assert(firstDog.breedTemperament == "Stubborn, Curious, Playful, Adventurous, Active, Fun-loving")
        assert(firstDog.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/1.jpg")

        position = 5
        val dog5 = list[position]
        assert(dog5.breedId == "6")
        assert(dog5.breedName == "Akita")
        assert(dog5.breedPurpose == "Hunting bears")
        assert(dog5.breedGroup == "Working")
        assert(dog5.breedLifespan == "10 - 14 years")
        assert(dog5.breedTemperament == "Docile, Alert, Responsive, Dignified, Composed, Friendly, Receptive, Faithful, Courageous")
        assert(dog5.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/6.jpg")

        position = 7
        val dog7 = list[position]
        assert(dog7.breedId == "8")
        assert(dog7.breedName == "Alaskan Husky")
        assert(dog7.breedPurpose == "Sled pulling")
        assert(dog7.breedGroup == "Mixed")
        assert(dog7.breedLifespan == "10 - 13 years")
        assert(dog7.breedTemperament == "Friendly, Energetic, Loyal, Gentle, Confident")
        assert(dog7.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/8.jpg")

        position = 123
        val dog123 = list[position]
        assert(dog123.breedId == "188")
        assert(dog123.breedName == "Pharaoh Hound")
        assert(dog123.breedPurpose == "Hunting rabbits")
        assert(dog123.breedGroup == "Hound")
        assert(dog123.breedLifespan == "12 - 14 years")
        assert(dog123.breedTemperament == "Affectionate, Sociable, Playful, Intelligent, Active, Trainable")
        assert(dog123.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/188.jpg")

        position = list.size - 1
        val lastDog = list[position]
        assert(lastDog.breedId == "264")
        assert(lastDog.breedName == "Yorkshire Terrier")
        assert(lastDog.breedPurpose == "Small vermin hunting")
        assert(lastDog.breedGroup == "Toy")
        assert(lastDog.breedLifespan == "12 - 16 years")
        assert(lastDog.breedTemperament == "Bold, Independent, Confident, Intelligent, Courageous")
        assert(lastDog.breedImageUrl == "https://raw.githubusercontent.com/DevTides/DogsApi/master/264.jpg")
    }

    @After
    fun teardown() {
        println("teardown")
    }
}
