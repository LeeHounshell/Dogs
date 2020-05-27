package com.harlie.dogs.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.viewmodel.DogDetailViewModel
import com.harlie.dogs.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private val _tag = "LEE: <" + DetailFragment::class.java.simpleName + ">"

    private lateinit var dogDetailViewModel: DogDetailViewModel
    private var dogUuid = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.tag(_tag).d("onCreateView")
        return inflater.inflate(R.layout.fragment_detail, container, false)
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
        dogDetailViewModel.fetch()
        observeViewModel()
    }

    private fun observeViewModel() {
        Timber.tag(_tag).d("observeViewModel")
        dogDetailViewModel.dogLiveDetail.observe(viewLifecycleOwner, Observer { dog ->
            Timber.tag(_tag).d("observe dogLiveDetail dog=${dog}")
            dog?.let {
                //FIXME: set dogDetailImage
                dogDetailName.text = dog.breedName
                dogDetailPurpose.text = dog.breedPurpose
                dogDetailTemperament.text = dog.breedTemperament
                dogDetailLifespan.text = dog.breedLifespan
/* FIXME
                buttonShowList.setOnClickListener {button ->
                    Timber.tag(tag).d("-CLICK- buttonShowList")
                    val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
                    button.findNavController().navigate(action)
                }
*/
            }
        })
    }

}
