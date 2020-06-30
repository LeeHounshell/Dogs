package com.harlie.dogs

import android.content.Context
import androidx.room.Room
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.room.DogDatabase
import com.harlie.dogs.util.GlideWrapper
import com.harlie.dogs.view.MainActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsEqual.equalTo
import org.junit.*
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class RoomDatabaseTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var db: DogDatabase

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
    fun createDb() {
        System.out.println("createDb")
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, DogDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        System.out.println("closeDb")
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeOneDogThenReadOneDog() {
        System.out.println("writeOneDogThenReadOneDog")
        val dogList: List<DogBreed> = TestUtil().createTestDogs(3)
        dogList.forEach {dog ->
            System.out.println("dog: ${dog}")
            runBlocking {
                db.dogDao().insertAll(dog) // inserts one dog
                val byId = db.dogDao().getDog(dog.uuid)
                assertThat(byId.uuid, equalTo(dog.uuid))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertDogsListThenReplaceAll() {
        System.out.println("insertDogsListThenReplaceAll")
        val dogList: List<DogBreed> = TestUtil().createTestDogs(3)
        val dogReplaceList: List<DogBreed> = TestUtil().createTestDogs(5)
        val dao = db.dogDao()
        runBlocking {
            val nodogs = dao.getAllDogs()
            assertThat(nodogs.size, equalTo(0))
            dao.insertAll(*dogList.toTypedArray())
            val threedogs = dao.getAllDogs()
            assertThat(threedogs.size, equalTo(3))
            dao.deleteAllDogs()
            val dogsgone = dao.getAllDogs()
            assertThat(dogsgone.size, equalTo(0))
            dao.insertAll(*dogReplaceList.toTypedArray())
            val fivedogs = dao.getAllDogs()
            assertThat(fivedogs.size, equalTo(5))
            dao.deleteAllDogs()
            val dogsagaingone = dao.getAllDogs()
            assertThat(dogsagaingone.size, equalTo(0))
            dao.insertAll(*dogList.toTypedArray())
            val threedogsagain = dao.getAllDogs()
            assertThat(threedogsagain.size, equalTo(3))
        }
    }

}
