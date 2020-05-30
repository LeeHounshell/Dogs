package com.harlie.dogs.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.model.DogsApiService
import com.harlie.dogs.repository.DogsListDataRepository
import com.harlie.dogs.util.RxErrorEvent
import com.harlie.dogs.viewmodel.DogsListViewModel
import com.harlie.dogs.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    private val _tag = "LEE: <" + ListFragment::class.java.simpleName + ">"

    private lateinit var dogListViewModel: DogsListViewModel
    private val dogListAdapter = DogsListAdapter(arrayListOf())

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.tag(_tag).d("onCreateView")
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
            Timber.tag(_tag).d("observe dogsLiveList size=${dogs?.size}")
            dogs?.let {
                dogsList.visibility = View.VISIBLE
                dogListAdapter.updateDogList(dogs)
                dogListViewModel.loadingComplete()
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

    private fun refresh() {
        Timber.tag(_tag).d("refresh")
        uiScope.launch(Dispatchers.IO) {
            dogListViewModel.refresh()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.tag(_tag).d("onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
        // this forces the RecyclerView to redraw images (to fix an Android rotation bug)
        val myAdapter = dogsList.adapter
        dogsList.adapter = myAdapter
    }

    override fun onDestroy(){
        Timber.tag(_tag).d("onDestroy")
        job.cancel()
        super.onDestroy()
    }

    override fun onStart() {
        Timber.tag(_tag).d("onStart")
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        Timber.tag(_tag).d("onStop")
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRxErrorEvent(rxError_event: RxErrorEvent) {
        Timber.tag(_tag).d("onRxErrorEvent")
        dogListViewModel.loadingComplete()
        Toast.makeText(activity, rxError_event.errorDescription, Toast.LENGTH_LONG).show()
    }

}
