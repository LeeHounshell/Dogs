package com.harlie.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber

import com.harlie.dogs.R
import com.harlie.dogs.repository.DataRepository
import com.harlie.dogs.viewmodel.DogListViewModel
import com.harlie.dogs.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    private val TAG = "LEE: <" + ListFragment::class.java.simpleName + ">"

    private lateinit var dogListViewModel: DogListViewModel
    private val dogListAdapter = DogListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.tag(TAG).d("onCreateView")
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = MyViewModelFactory(DataRepository())
        dogListViewModel = ViewModelProvider(this, viewModelFactory).get(DogListViewModel::class.java)
        dogListViewModel.refresh()
        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        Timber.tag(TAG).d("observeViewModel")
        dogListViewModel.dogsLiveList.observe(viewLifecycleOwner, Observer { dogs ->
            Timber.tag(TAG).d("observe dogsLiveList size=${dogs?.size}")
            dogs?.let {
                dogsList.visibility = View.VISIBLE
                dogListAdapter.updateDogList(dogs)
            }
        })
        dogListViewModel.dogsLoadError.observe(viewLifecycleOwner, Observer { isError ->
            Timber.tag(TAG).d("observe dogsLoadError=${isError}")
            isError?.let {
                dogsLoadingError.visibility = if (it) View.VISIBLE else View.INVISIBLE
            }
        })
        dogListViewModel.dogsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Timber.tag(TAG).d("observe dogsLoading=${isLoading}")
            isLoading?.let {
                dogsLoadingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
                if (it) {
                    dogsLoadingError.visibility = View.INVISIBLE
                    dogsList.visibility = View.INVISIBLE
                }
            }
        })
    }

/* FIXME
        buttonShowDetail.setOnClickListener {button ->
            Timber.tag(TAG).d("-CLICK- buttonShowDetail")
            val action = ListFragmentDirections.actionListFragmentToDetailFragment()
            val dogUuid = 543210 // FIXME
            action.dogUuid = dogUuid
            button.findNavController().navigate(action)
        }
*/

}
