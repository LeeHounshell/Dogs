package com.harlie.dogs.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogsApiService
import com.harlie.dogs.repository.DogsListDataRepository
import com.harlie.dogs.util.navigateSafe
import com.harlie.dogs.viewmodel.DogsListViewModel
import com.harlie.dogs.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ListFragment : Fragment() {
    private val _tag = "LEE: <" + ListFragment::class.java.simpleName + ">"

    private lateinit var dogListViewModel: DogsListViewModel
    private val dogListAdapter = DogsListAdapter(arrayListOf())
    private var currentDogs: List<DogBreed> = emptyList()

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.tag(_tag).d("onCreateView")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag(_tag).d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val repositoryURL: String = DogsApiService.BASE_URL
        val viewModelFactory = MyViewModelFactory(DogsListDataRepository(repositoryURL))
        dogListViewModel = ViewModelProvider(this, viewModelFactory).get(DogsListViewModel::class.java)
        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        observeViewModel()

        refreshLayout.setOnRefreshListener {
            Timber.tag(_tag).d("swipe to refresh")
            dogsList.visibility = View.INVISIBLE
            dogsLoadingProgress.visibility = View.VISIBLE
            refreshLayout.isRefreshing = false
            refresh()
        }

        refresh()
    }

    private fun observeViewModel() {
        Timber.tag(_tag).d("observeViewModel")
        dogListViewModel.dogsList.observe(viewLifecycleOwner, Observer { dogs ->
            Timber.tag(_tag).d("observeViewModel: observe dogsLiveList size=${dogs?.size}")
            dogs?.let {
                if (dogs.size != 0) {
                    currentDogs = dogs
                    showCurrentDogs()
                    dogListViewModel.loadingComplete()
                }
                else {
                    Timber.tag(_tag).d("observeViewModel: need to refresh()")
                    refresh()
                }
            }
        })
        dogListViewModel.dogsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Timber.tag(_tag).d("observe dogsLoading=${isLoading}")
            isLoading?.let {
                dogsLoadingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
                if (it) {
                    dogsList.visibility = View.INVISIBLE
                }
            }
        })
    }

    fun showCurrentDogs() {
        val haveUuids = (currentDogs != null && currentDogs.size > 0 && currentDogs[0].uuid != 0)
        Timber.tag(_tag).d("showCurrentDogs: haveUuids=${haveUuids}")
        if (! haveUuids) {
            refresh()
        }
        else {
            // this forces the RecyclerView to redraw images (to fix an Android rotation bug)
            val myAdapter = dogsList.adapter
            dogsList.adapter = myAdapter
            // set adapter data as current dogs list
            dogListAdapter.updateDogList(currentDogs)
            dogsList.visibility = View.VISIBLE
        }
    }

    fun refresh() {
        Timber.tag(_tag).d("refresh")
        uiScope.launch(Dispatchers.IO) {
            dogListViewModel.refresh()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.tag(_tag).d("onCreateOptionsMenu")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.tag(_tag).d("onOptionsItemSelected")
        when (item.itemId) {
            R.id.actionSettings -> {
                view?.let {
                    Timber.tag(_tag).d("onOptionsItemSelected: navigate to SettingsFragment")
                    val action = ListFragmentDirections.actionListFragmentToSettingsFragment()
                    it.findNavController().navigateSafe(action)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.tag(_tag).d("onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
        showCurrentDogs()
        dogListViewModel.loadingComplete()
    }

    override fun onDestroy(){
        Timber.tag(_tag).d("onDestroy")
        job.cancel()
        super.onDestroy()
    }

}
