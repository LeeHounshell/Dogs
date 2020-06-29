package com.harlie.dogs

import com.harlie.dogs.model.DogBreed

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

}
