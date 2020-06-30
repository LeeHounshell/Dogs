package com.harlie.dogs

import androidx.lifecycle.MutableLiveData
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.ActivityTestRule
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogsApi
import com.harlie.dogs.model.DogsApiService
import com.harlie.dogs.model.DogsApiService.Companion.BASE_URL
import com.harlie.dogs.repository.DogsListDataRepository
import com.harlie.dogs.util.GlideWrapper
import com.harlie.dogs.util.SharedPreferencesHelper
import com.harlie.dogs.util.postDefault
import com.harlie.dogs.view.MainActivity
import com.harlie.dogs.viewmodel.DogsListViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class DogsListViewModelTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    val dummyData = MutableLiveData<List<DogBreed>>().postDefault(TestUtil().createTestDogs(3))

    @MockK
    lateinit var apiService: DogsApiService
    @MockK
    lateinit var dummyApi: DogsApi
    @MockK
    lateinit var prefHelper: SharedPreferencesHelper

    lateinit var viewModel: DogsListViewModel
    lateinit var dogsListDataRepository: DogsListDataRepository

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
        MockKAnnotations.init(this)
    }

    @Test
    fun test_That_LiveData_Takes_on_Values_from_Repository() {
        println("test_That_LiveData_Takes_on_Values_from_Repository")

        every { apiService.getRequestApi() } returns dummyApi
        every { apiService.setRealBaseUrl(any()) } returns Unit
        every { prefHelper.isDatabaseCreated() } returns false
        every { prefHelper.markDatabaseCreated() } returns Unit
        every { prefHelper.saveUpdateTime(any()) } returns Unit
        every { prefHelper.getCacheDuration() } returns "600"
        every { prefHelper.getUpdateTime() } returns 0

        dogsListDataRepository = spyk(DogsListDataRepository(BASE_URL, apiService, prefHelper))

        coEvery { dogsListDataRepository.fetchFromDatabase(any()) } returns dummyData
        coEvery { dogsListDataRepository.fetchFromRemote() } returns dummyData
        coEvery { dogsListDataRepository.storeDogsLocally(any()) } returns Unit

        viewModel = DogsListViewModel(dogsListDataRepository)

        runBlocking {
            UiThreadStatement.runOnUiThread {
                viewModel.dogsList.observeForever {
                    println("*** viewModel.dogsList.size=${viewModel.dogsList.value?.size}")
                    assert(it.size == 3)
                    val dog1 = it[0]
                    assert(dog1.breedId == "1")
                    assert(dog1.breedGroup == "group1")
                    assert(dog1.breedName == "name1")
                    assert(dog1.breedPurpose == "purpose1")
                    assert(dog1.breedLifespan == "lifespan1")
                    assert(dog1.breedImageUrl == "image1")
                    assert(dog1.breedTemperament == "temperament1")
                    val dog2 = it[1]
                    assert(dog2.breedId == "2")
                    assert(dog2.breedGroup == "group2")
                    assert(dog2.breedName == "name2")
                    assert(dog2.breedPurpose == "purpose2")
                    assert(dog2.breedLifespan == "lifespan2")
                    assert(dog2.breedImageUrl == "image2")
                    assert(dog2.breedTemperament == "temperament2")
                    val dog3 = it[2]
                    assert(dog3.breedId == "3")
                    assert(dog3.breedGroup == "group3")
                    assert(dog3.breedName == "name3")
                    assert(dog3.breedPurpose == "purpose3")
                    assert(dog3.breedLifespan == "lifespan3")
                    assert(dog3.breedImageUrl == "image3")
                    assert(dog3.breedTemperament == "temperament3")
                }
            }
        }
    }

    @After
    fun teardown() {
        println("teardown")
    }
}
