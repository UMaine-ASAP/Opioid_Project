package com.asap.mindfulness.Setup


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.fragment_setup_user.*
import kotlinx.android.synthetic.main.fragment_setup_user.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SetupUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupUserFragment : Fragment() {
    var patientName: String? = ""
        get() = patient_name.text.toString()
    var patientPasswordEnabled = true
        get() = patient_password_switch.isChecked
    var patientPassword: String? = ""
        get() = patient_password.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_setup_user, container, false)

        rootView.patient_password_switch.setOnCheckedChangeListener { _, b ->
            patient_password.inputType = if (b) {
                81      // Password Field
            } else {
                0       // Disabled
            }
        }

        return rootView
    }

    companion object {
        fun newInstance() = SetupUserFragment()
    }

}// Required empty public constructor
