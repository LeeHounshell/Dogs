package com.harlie.dogs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.repository.DataRepository
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.repository.DogsListDataRepository

// This ViewModelFactory allows creation of ViewModels with parameters passed as arguments
// The advantage here is dependency inversion so that ViewModels do not create their repositories
class MyViewModelFactory constructor(private val repository: DataRepository): ViewModelProvider.Factory {
    private val _tag = "LEE: <" + MyViewModelFactory::class.java.simpleName + ">"

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Timber.tag(_tag).d("create $modelClass")
        try {
            return when (repository) {
                is DogsListDataRepository ->
                    modelClass.getConstructor(DogsListDataRepository::class.java)
                        .newInstance(repository)
                is DogDetailDataRepository ->
                    modelClass.getConstructor(DogDetailDataRepository::class.java)
                        .newInstance(repository)
                else ->
                    throw RuntimeException("modelClass is not a DataRepository: $modelClass")
            }
        }
        catch (e: InstantiationException) {
            throw RuntimeException("Can't create instance of $modelClass", e)
        }
        catch (e: IllegalAccessException) {
            throw RuntimeException("Can't create instance of $modelClass", e)
        }
    }

}
