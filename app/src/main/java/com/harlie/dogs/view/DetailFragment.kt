package com.harlie.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.github.ajalt.timberkt.Timber

import com.harlie.dogs.R
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private val TAG = "LEE: <" + DetailFragment::class.java.getSimpleName() + ">"

    private var dogUuid = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Timber.tag(TAG).d("onCreateView")
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            dogUuid = DetailFragmentArgs.fromBundle(bundle).dogUuid
        }
        Timber.tag(TAG).d("dogUuid=%d", dogUuid)

        buttonShowList.setOnClickListener {button ->
            Timber.tag(TAG).d("-CLICK- buttonShowList")
            val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
            button.findNavController().navigate(action)
        }
    }

}
