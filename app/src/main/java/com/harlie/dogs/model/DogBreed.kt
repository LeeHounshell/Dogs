package com.harlie.dogs.model

import com.google.gson.annotations.SerializedName

data class DogBreed(
    @SerializedName("id")
    val breedId: String?,

    @SerializedName("name")
    val breedName: String?,

    @SerializedName("life_span")
    val breedLifespan: String?,

    @SerializedName("breed_group")
    val breedGroup: String?,

    @SerializedName("bred_for")
    val breedPurpose: String?,

    @SerializedName("temperament")
    val breedTemerament: String?,

    @SerializedName("url")
    val breedImageUrl: String?
)
