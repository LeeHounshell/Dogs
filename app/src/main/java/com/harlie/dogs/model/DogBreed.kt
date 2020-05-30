package com.harlie.dogs.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class DogBreed(
    @ColumnInfo(name = "breed_id")
    @SerializedName("id")
    val breedId: String?,

    @ColumnInfo(name = "breed_name")
    @SerializedName("name")
    val breedName: String?,

    @ColumnInfo(name = "breed_lifespan")
    @SerializedName("life_span")
    val breedLifespan: String?,

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    val breedGroup: String?,

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    val breedPurpose: String?,

    @ColumnInfo(name = "breed_temperament")
    @SerializedName("temperament")
    val breedTemperament: String?,

    @ColumnInfo(name = "breed_image_url")
    @SerializedName("url")
    val breedImageUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

data class DogPalette(var color: Int)
