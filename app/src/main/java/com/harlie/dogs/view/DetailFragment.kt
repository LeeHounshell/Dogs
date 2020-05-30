package com.harlie.dogs.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.databinding.FragmentDetailBinding
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.util.getProgressDrawable
import com.harlie.dogs.util.loadImage
import com.harlie.dogs.viewmodel.DogDetailViewModel
import com.harlie.dogs.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private val _tag = "LEE: <" + DetailFragment::class.java.simpleName + ">"

    private lateinit var dogDetailViewModel: DogDetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding
    private var dogUuid = 0

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.tag(_tag).d("onCreateView")
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag(_tag).d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            dogUuid = DetailFragmentArgs.fromBundle(bundle).dogUuid
        }
        Timber.tag(_tag).d("dogUuid=%d", dogUuid)
        val viewModelFactory = MyViewModelFactory(DogDetailDataRepository(dogUuid))
        dogDetailViewModel = ViewModelProvider(this, viewModelFactory).get(DogDetailViewModel::class.java)

        observeViewModel()
        refresh()
    }

    private fun observeViewModel() {
        Timber.tag(_tag).d("observeViewModel")
        dogDetailViewModel.dog.observe(viewLifecycleOwner, Observer { dog ->
            Timber.tag(_tag).d("observe dog dog=${dog}")
            dataBinding.dog = dog
        })
    }

    private fun refresh() {
        Timber.tag(_tag).d("refresh")
        uiScope.launch(Dispatchers.IO) {
            dogDetailViewModel.fetch()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.tag(_tag).d("onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
        // FIXME: force redraw of the dog image (to fix an Android rotation bug)
    }

}
