package com.harlie.dogs.room

import androidx.room.*
import com.harlie.dogs.model.DogBreed

@Dao
interface DogDao {
    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query("SELECT * FROM dogbreed")
    suspend fun getAllDogs(): List<DogBreed>

    @Query("SELECT * FROM dogbreed WHERE breed_id = :dogId")
    suspend fun getDog(dogId: String): DogBreed

    @Query("DELETE FROM dogbreed")
    suspend fun deleteAllDogs()

    @Update
    suspend fun updateDog(dog: DogBreed)

    @Delete
    suspend fun deleteDog(dog: DogBreed)
}