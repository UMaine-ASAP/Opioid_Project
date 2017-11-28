package com.asap.mindfulness.Setup


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.fragment_setup_patient.*


/**
 * A simple [Fragment] subclass.
 * Use the [SetupPatientIdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupPatientIdFragment : Fragment() {
    var patientId: String? = ""
        get() = patient_id.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_setup_patient, container, false)
    }

    companion object {
        fun newInstance() = SetupPatientIdFragment()
    }

}// Required empty public constructor
