package com.harlie.dogs

import androidx.test.espresso.matcher.ViewMatchers
import com.harlie.dogs.model.DogBreed
import org.hamcrest.CoreMatchers
import org.junit.Assert

class TestUtil {

    fun createTestDogs(count: Int): List<DogBreed> {
        val dogsList = ArrayList<DogBreed>()
        var i = 1
        while (i <= count) {
            val dog = DogBreed("id$i", "name$i", "lifespan$i", "group$i", "purpose$i", "temperament$i", "image$i" )
            dog.uuid = i
            dogsList.add(dog)
            ++i
        }
        return dogsList
    }

    fun waitForViewToAppear() {
        System.out.println("waitForViewToAppear")
        Assert.assertTrue(
            ViewSynchronizer.viewExists(
                CoreMatchers.allOf(
                    ViewMatchers.withId(R.id.dogsList), ViewMatchers.withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE
                    )
                ), 10000
            )
        );
        slowDownSoWeCanSeeTheUI()
    }

    fun slowDownSoWeCanSeeTheUI() {
        System.out.println("slowDownSoWeCanSeeTheUI")
        Thread.sleep(5000)
    }

}
