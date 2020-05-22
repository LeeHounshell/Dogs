package com.harlie.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.github.ajalt.timberkt.Timber

import com.harlie.dogs.R
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    private val TAG = "LEE: <" + ListFragment::class.java.getSimpleName() + ">"

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
        buttonShowDetail.setOnClickListener {button ->
            Timber.tag(TAG).d("-CLICK- buttonShowDetail")
            val action = ListFragmentDirections.actionListFragmentToDetailFragment()
            val dogUuid = 543210 // FIXME
            action.dogUuid = dogUuid
            button.findNavController().navigate(action)
        }
    }

}
