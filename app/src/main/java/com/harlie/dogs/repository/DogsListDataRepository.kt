package com.harlie.dogs.repository

import com.github.ajalt.timberkt.Timber

class DogsListDataRepository(repositoryURL: String): DataRepository() {
    private val TAG = "LEE: <" + DogsListDataRepository::class.java.simpleName + ">"

    private val dogURL: String

    init {
        Timber.tag(TAG).d("init $repositoryURL")
        dogURL = repositoryURL
    }

}
